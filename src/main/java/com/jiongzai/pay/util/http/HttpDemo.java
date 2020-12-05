package com.jiongzai.pay.util.http;

import com.jiongzai.pay.util.http.Http.Response;

public class HttpDemo {


    public static void main(String[] args) {
    	Response res=Http.create("http://103.45.108.16:8899/hk?appid=1252323825&asig=sPnLN0_jYQdQzqok70zPK_EM_NSLWLcoBvpIkEL9KXvJ66cq5eSob1CInVLz6ACrnXLEr2-fapI39hri6M9UlEq1ViKuz7m5").get().send().getResponse();
    	System.out.println(res.getResult());
    	
    }
}
