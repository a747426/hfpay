package com.jiongzai.pay.util.security;

import ch.qos.logback.core.util.FileUtil;
import org.apache.commons.io.FileUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

public class RSAJS {

    public static String excuteJs(String vcode) throws ScriptException, FileNotFoundException, NoSuchMethodException {

        ScriptEngineManager engineManager = new ScriptEngineManager();

        ScriptEngine engine = engineManager.getEngineByName("JavaScript"); // 得到脚本引擎

        String reader = null;
        //获取文件所在的相对路径
        //String text = System.getProperty("user.dir");
        //reader = text + "\\src\\main\\resources\\test.js";
        //String path = RSAJS.class.getResource("/").getPath();

        //reader = path + "/static/js/security.js";
        InputStream stream = RSAJS.class.getClassLoader().getResourceAsStream("static/js/security.js");
        File file = new File("security.js");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(stream, file);
        } catch (IOException e) {
            return  "文件错误";
        }
        FileReader fReader = new FileReader("security.js");
        engine.eval(fReader );

        Invocable inv = (Invocable) engine;
        //调用js中的方法
        Object test2 = inv.invokeFunction("rsa",vcode);
        String url = test2.toString();
        try{
            fReader.close();
        }catch(Exception e){
        }

        return url;
    }
}
