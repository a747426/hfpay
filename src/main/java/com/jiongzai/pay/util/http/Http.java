package com.jiongzai.pay.util.http;

import static org.apache.commons.lang.StringUtils.contains;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.StringUtils.lowerCase;
import static org.apache.commons.lang.StringUtils.trim;
import static org.apache.commons.lang.StringUtils.upperCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import com.jiongzai.pay.service.impl.IpPoolServiceImpl;
import com.jiongzai.pay.util.json.JSONUtil;

/**
 * 封装原生的请求
 *
 *
 *
 *
 */
public class Http<T> implements Cloneable{

	private static final Logger log = LogManager.getLogger(IpPoolServiceImpl.class);
	
    static final Class<Http> CLASS = Http.class;

    private Map<String,String> heads = new LinkedHashMap<>();

    private Map<String,String> body = new LinkedHashMap<>();
    /**
     * JSON 传参
     */
    private String bodys;

    private int timeout;

    private String url;
    /**cookis*/
    private String cookies;
    /**编码，默认"UTF-8"*/
    private String charset = "UTF-8";
    /**链接超时时间，默认5s*/
    private int connectTimeout = 5000;
    /**读取超时时间，默认3s**/
    private int readTimeout = 3000;
    //当前请求http 状态
    private int status;
    private String message;
    //消耗时间
    private double time;
    //目标 IP
    private String targetIp;
    //请求了几次
    private int count;
    //设置请求几次，default 1
    private int requestCount = 1;
    
    private boolean redirect=true;
    
    private Http<T> reconnect;
  //代理
    private HttpProxy proxy;
    //返回值
    private Response<T> response = new Response<>();


    //允许请求多大 {单位 kb}
    private int maxContentLength;
    //允许返回的类型
    private List<String> checkContentType = new LinkedList<>();

    /**
     *     请求方式
     *     Default GET
     */
    private Method method = Method.GET;
    /**
     *     提交方式
     *     Default FORM
     */
    private RequestType requestType = RequestType.FORM;

    public Http() {

    }

    public static Http create(String url){
        Http http = new Http();
        http.setUrl(url);
        return http;
    }
    public static Http create(String url,String...parms){
        Http http = new Http();
        if(null != parms && parms.length>0){
            url = String.format(url,parms);
        }
        http.setUrl(url);
        return http;
    }

    public Http post(){
        this.method = Method.POST;
        return this;
    }
    public Http get(){
        this.method = Method.GET;
        return this;
    }
    public Http charset(String charset){
        this.charset = charset;
        return this;
    }

    /**
     * 1.判断当前请求要不要重试
     * 2.如果要重试，把重试的{reconnect} ++ 。
     * 3.重试
     * @return
     */
    public Http reconnect(){
        log.info("重试中，共：{}次，当前次数：{}次",requestCount,count);
        //重试次数大于1，并且重试次数大于记录数
        if(requestCount > 1 && requestCount > count){
            try {
                this.reconnect = (Http)this.clone();
            } catch (CloneNotSupportedException e) {
                log.error("clone 对象失败",e);
            }
            this.send();
        }
        return this;
    }
    /**
     * 字符串转urlcode
     *
     * @param value
     * @return
     */
    public static String strToUrlcode(String value) {
        try {
            value = java.net.URLEncoder.encode(value, "utf-8");
            return value;
        } catch (UnsupportedEncodingException e) {
            log.error("字符串转换为URLCode失败,value:{}",value, e);
            e.printStackTrace();
            return null;
        }
    }

    public Http send()  {

        //重试次数大于1，并且重试次数大于记录数
        if(requestCount > 1 && requestCount > count){
            try {
                this.reconnect = (Http)this.clone();
            } catch (CloneNotSupportedException e) {
                log.error("clone 对象失败",e);
            }
            count += 1;
        }

        HttpURLConnection conn = null;
        OutputStream outStream = null;
        OutputStreamWriter  out = null;
        InputStream inStream   = null;
        BufferedReader in =      null;
        URL realUrl;
        double begin = System.currentTimeMillis();
        try {
            realUrl = new URL(url);
            if("https".equalsIgnoreCase(realUrl.getProtocol())){
                SslUtils.ignoreSsl();
            }
            //设置代理
            if(this.proxy == null||StringUtils.isEmpty(proxy.getHost())||proxy.getPort()==0){
            	conn = (HttpURLConnection) realUrl.openConnection();
            }else{
            	 InetSocketAddress addr = new InetSocketAddress(proxy.getHost(),proxy.getPort());
                 Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
                  // 打开和URL之间的连接
                 conn = (HttpURLConnection) realUrl.openConnection(proxy);
            }

            // 发送POST请求必须设置如下两行
            conn.setRequestMethod(upperCase(method.get()));// 提交模式


            conn.setUseCaches(false);
            conn.setDoOutput(method.isPost());//如果是post使用true。
            conn.setDoInput(true);
            if(!this.redirect)
            {
            	conn.setFollowRedirects(false);
            }

            conn.setConnectTimeout(connectTimeout);  //设置连接主机超时（单位：毫秒）
            conn.setReadTimeout(readTimeout);     //设置从主机读取数据超时（单位：毫秒）
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(connectTimeout));
            System.setProperty("sun.net.client.defaultReadTimeout",  String.valueOf(readTimeout));

            heads.put("Connection", "close");
            

            //判断如果是批量提交就采用这种方式（JSON传输）
            if(RequestType.JSON.equals(this.requestType)){
                heads.put("Content-Type",lowerCase(String.format("application/json; charset=%s",charset)));
                heads.put("x-requested-with","XMLHttpRequest");
            }else{
                heads.put("Content-Type","application/x-www-form-urlencoded");
            }

            if(isNotBlank(cookies)){
                heads.put("Cookie",cookies );
            }

            //设置参数
            List<String> sbp = new ArrayList<>();
            for(Map.Entry<String,String> entry :  body.entrySet()){
                sbp.add(String.format("%s=%s",entry.getKey(), strToUrlcode(entry.getValue())));
            }
            //设置请求头
            for(Map.Entry<String,String> entry : heads.entrySet()){
                conn.setRequestProperty(entry.getKey(),entry.getValue());
            }

            if(method.isPost() && sbp.size() > 0 ){
                String parameter = join(sbp,"&");
//                heads.put("Content-Length",String.valueOf(parameter.length()));
                outStream = conn.getOutputStream();
                out = new OutputStreamWriter(outStream);
                out.write(parameter);

                //JSON & Post    && RequestType.JSON.equals(this.requestType)
            }else if (method.isPost()  && isNotBlank(bodys)){
//                heads.put("Content-Length",String.valueOf(bodys.length()));
                outStream = conn.getOutputStream();
                out = new OutputStreamWriter(outStream);
                out.write(bodys);
            }
            if(null != out){
                // flush输出流的缓冲
                out.flush();
            }

            response = new Response(conn.getHeaderFields());
            //对方返回的状态
            response.setStatus(conn.getResponseCode());

            if(maxContentLength > 0 ){
                if(conn.getContentLength() > maxContentLength * 1024 ){
                    this.message = String.format("您的请求已经超过限制：%s",maxContentLength);
                    this.status  = 403;
                    return this;
                }
            }
            if(checkContentType.size() > 0 && !checkContentType.contains(trim(conn.getContentType()))){
                this.message = String.format("您的请求已经超过限制：%s",maxContentLength);
                this.status  = 403;
                return this;
            }

            //TODO 请求大小判断，请求类型判断

            //如果有异常信息，也带回去
            String result = "";
            try{
                //正常（status=200）返回值
                inStream = conn.getInputStream();
            }catch (IOException ioe){
                //异常（status!=200）返回值
                inStream = conn.getErrorStream();
            }

            if(null != inStream){
                //判断需不需要解压缩
                if(response.isZip){
                    inStream = new GZIPInputStream(inStream);
                }
                try{
                    result = IOUtils.toString(inStream,charset);
                }catch (Exception e) {
                    StringBuffer sb = new StringBuffer();
                    in = new BufferedReader(new InputStreamReader(inStream, charset));
                    String line = "";
                    while ((line = in.readLine()) != null){
                        sb.append(line);
                    }
                    result = sb.toString();
                }
            }
            response.setResult(result);
            //TODO 泛型判断，如果不是 String 和 Object 那就给他转换，并且不报错try，
            /**
             try{
             T t = new Gson().fromJson(result,new TypeToken<T>() {}.getType());
             response.setData(t);
             }catch (Exception e){
             LoggerUtils.fmtDebug(getClass(),"转换对象失败：%s",url);
             }
             **/

        } catch (UnknownHostException e) {
            //域名错误
            this.message = "请输入正确的网址";
            log.info("网址异常{}", url);
            reconnect();
        } catch (SocketTimeoutException e) {
            //超时
            this.message = String.format("请求地址超时{}", url);
            log.info( "请求地址超时{}", url);
            reconnect();
        } catch (Exception e) {
            //其他异常
            this.message ="请求出现未知异常，请重试，多次出现请联系站长。";
            log.info( "请求出现未知异常，请重试！{}", url);
            reconnect();
        }finally{
            realUrl = null;
            try {
                if(null != conn)conn.disconnect();
                conn = null;
            } catch (Exception e2) {
            }
            try {
                if(null != outStream)outStream.close();
                outStream = null;
            } catch (Exception e2) {
            }
            try {
                if(null != out)out.close();
                out = null;
            } catch (Exception e2) {
            }
            try {
                if(null != inStream)inStream.close();
                inStream = null;
            } catch (Exception e2) {
            }
            try {
                if(null != in)in.close();
                in = null;
            } catch (Exception e2) {
            }


        }
        this.time =  ( System.currentTimeMillis()- begin) / 1000;
        this.status = 200;

        return this;
    }

    public Http<T> requestCount(int requestCount) {
        this.requestCount = requestCount;
        return this;
    }

    public Http requestType(RequestType type) {
        this.requestType = type;
        return this;
    }

    public Http baseHead() {
    	heads.put("accept", "*/*");
    	heads.put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
    	heads.put("cache-control", "no-cache");
    	heads.put("pragma", "no-cache");
    	heads.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36");
        return this;
    }

    public enum Method{
        POST,GET;
        public boolean isPost(){
            return this.equals(POST);
        }

        public String get(){
            return this.name();
        }

    }
    public enum RequestType{
        JSON,FORM;
        public boolean isJSON(){
            return this.equals(JSON);
        }

        public String get(){
            return this.name();
        }
    }

    //HTTP 请求相关的工具类
    public static class Utils{


        public static Map<String,String> cookieToMap(String cookies){
            //封装cookie的值为一个有序map，方便查找对应的cookie

            String[] cs = cookies.split(";");
            Map<String,String> cookieMap = new LinkedHashMap<>();

            for (String c : cs) {
                if(contains(c,"=")){
                    String[] kv = c.split("=");
                    //处理相同key的cookie，优先保留有值的。
                    if(!cookieMap.containsKey(kv[0]) || isBlank(cookieMap.get(kv[0]))){
                        //有空cookie
                        cookieMap.put(kv[0],kv.length > 1 ? kv[1] : "");
                    }


                }
            }
            return cookieMap;
        }
    }
    /**
     * 返回值
     */
    public static class Response<T>{
        private Map<String,String> heads = new LinkedHashMap<>();

        //当前请求http 状态
        private int status;

        private String result;

        private String cookies;

        private Map<String,String> cookieMap = new LinkedHashMap<>();
        private T data;
        //是不是压缩了
        private boolean isZip = Boolean.FALSE;
        Response(){}
        Response(Map<String, List<String>> headMap){

            if(null != headMap){
                StringBuffer sb = new StringBuffer();
                for(Map.Entry<String, List<String>> e : headMap.entrySet()){
                    //GZIP 解压缩
                    if("gzip".equalsIgnoreCase(trim(e.getValue().get(0)))){
                        isZip = Boolean.TRUE;
                    }
                    String key = e.getKey();
                    if("cookie".equalsIgnoreCase(key) || "set-cookie".equalsIgnoreCase(key)){

                        for (String ele : e.getValue()) {
                            //这里出现了 cookie 的生命周期，path 等等属性，这些属性是我们不需要得。
                            if(contains(ele,";")){
                                ele = ele.split(";")[0];
                            }
                            sb.append(ele).append(";");
                        }
                    }
                    heads.put(key,join(e.getValue(),","));
                }
                if(sb.length() > 0 ){
                    //String 形态的cookie
                    cookieMap = Http.Utils.cookieToMap(sb.toString());
                    cookies = cookieMap.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(";"));
                }
            }


        }

        public Map<String, String> getHeads() {
            return heads;
        }

        public void setHeads(Map<String, String> heads) {
            this.heads = heads;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getCookies() {
            return cookies;
        }

        public void setCookies(String cookies) {
            this.cookies = cookies;
        }

        public Map<String, String> getCookieMap() {
            return cookieMap;
        }

        public void setCookieMap(Map<String, String> cookieMap) {
            this.cookieMap = cookieMap;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public boolean isZip() {
            return isZip;
        }

        public void setZip(boolean zip) {
            isZip = zip;
        }
    }





    public Http<T> timeout(int i) {
        this.connectTimeout = i * 1000;
        return this;
    }
    public Http<T> readTimeout(int i) {
        this.readTimeout = i * 1000;
        return this;
    }
    
    public Http<T> redirect(boolean set) {
        this.redirect = set;
        return this;
    }
    
    public Http<T> proxy(String host,int port) {
        this.setProxy(new HttpProxy(host,port));
        return this;
    }
    
    public HttpProxy getProxy() {
		return proxy;
	}

	public void setProxy(HttpProxy proxy) {
		this.proxy = proxy;
	}

	public Http<T> cookies(String cookies) {
        this.setCookies(cookies);
        return this;
    }
    public Http<T> head(String key, String value) {
        this.heads.put(key,value);
        return this;
    }
    public Http<T> heads(Map heads) {
        this.heads.putAll(heads);
        return this;
    }
    public Map<String,String> head() {
        return getHeads();
    }
    public Http<T>  body(String key, String value) {
        this.body.put(key,value);
        return this;
    }
    public Http<T>  body(Map body) {
        this.body.putAll(body);
        return this;
    }
    public Http<T>  bodys(Object bodys) {


        if(null !=  bodys && bodys instanceof  String){
            this.bodys = (String) bodys;
        }else{
            //this.bodys = new Gson().toJson(bodys);
        	this.bodys =JSONUtil.toJson(bodys);
        }
        this.head("Content-Length",""+this.bodys.length());
        return this;
    }

    public Map<String, String> getHeads() {
        return heads;
    }

    public void setHeads(Map<String, String> heads) {
        this.heads = heads;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public void setBody(Map<String, String> body) {
        this.body = body;
    }

    public String getBodys() {
        return bodys;
    }

    public void setBodys(String bodys) {
        this.bodys = bodys;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public Http<T> getReconnect() {
        return reconnect;
    }

    public void setReconnect(Http<T> reconnect) {
        this.reconnect = reconnect;
    }

    public Response<T> getResponse() {
        return response;
    }

    public void setResponse(Response<T> response) {
        this.response = response;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public List<String> getCheckContentType() {
        return checkContentType;
    }

    public void setCheckContentType(List<String> checkContentType) {
        this.checkContentType = checkContentType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}