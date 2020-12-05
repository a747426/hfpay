package com.jiongzai.pay.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jiongzai.pay.model.UserBean;
import com.jiongzai.pay.util.HttpUtil;
import com.jiongzai.pay.util.json.JSONUtil;

@Component
public class AuthInterceptor implements HandlerInterceptor {
	
	private Log log = LogFactory.getLog(AuthInterceptor.class);
	
    public AuthInterceptor() {  
        // TODO Auto-generated constructor stub  
    }  
  
    /** 
     * 在业务处理器处理请求之前被调用 
     * 如果返回false 
     *     从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链 
     *  
     * 如果返回true 
     *    执行下一个拦截器,直到所有的拦截器都执行完毕 
     *    再执行被拦截的Controller 
     *    然后进入拦截器链, 
     *    从最后一个拦截器往回执行所有的postHandle() 
     *    接着再从最后一个拦截器往回执行所有的afterCompletion() 
     */  
    @Override  
    public boolean preHandle(HttpServletRequest request,  
            HttpServletResponse response, Object handler) throws Exception {
    	String ip=HttpUtil.getIpAddr(request);
    	log.info("request ip:"+ip);
    	String requestURL = request.getRequestURL().toString();
    	//log.info("request url:"+requestURL);
    	/*Map<String,String> requestParam=HttpUtil.getParameterMap(request);
    	log.info("request param:"+JSONUtil.toJson(requestParam));*/
		String path = request.getServletPath();
		if (path.startsWith("/testPay.html") || path.startsWith("/cgi-bin") || path.startsWith("/api") || path.startsWith("/user/login.html") || path.startsWith("/js") || path.startsWith("/loginPage.html")) {

			if (requestURL.contains("cdkey")) {
				return false;
			}
			return true;
		}
        Object checkIp = request.getSession().getAttribute("ip");
        if (checkIp != null) {
            if (!ip.equals(checkIp.toString())){
                log.info("ip 发送了变化自动退出"+ ip + checkIp.toString());
                return  false;
            }
        }

		UserBean user=(UserBean) request.getSession().getAttribute("auser");
    	if(user==null)
        {
           response.sendRedirect(request.getContextPath()+"/loginPage.html");
           return false;
        }
    	if(!StringUtils.isEmpty(user.getBind_ip())&&!user.getBind_ip().contains(ip))
    	{
    		return false;
    	}
    	log.info("request admin:"+user.getId());
        return true; 
    }  
  
    //在业务处理器处理请求执行完成后,生成视图之前执行的动作   
    @Override  
    public void postHandle(HttpServletRequest request,  
            HttpServletResponse response, Object handler,  
            ModelAndView modelAndView) throws Exception {  
        // TODO Auto-generated method stub  
//        System.out.println("==============执行顺序: 2、postHandle================"); 
    }  
    
  
    /** 
     * 在DispatcherServlet完全处理完请求后被调用  
     *  
     *   当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion() 
     */  
    @Override  
    public void afterCompletion(HttpServletRequest request,  
            HttpServletResponse response, Object handler, Exception ex)  
            throws Exception {  
        // TODO Auto-generated method stub  
//        System.out.println("==============执行顺序: 3、afterCompletion================");  
    }  
}
