package com.jiongzai.pay.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jiongzai.pay.common.Result;
import com.jiongzai.pay.model.ConfigBean;
import com.jiongzai.pay.service.ConfigService;

@Controller
@RequestMapping("/config")
public class ConfigController {
	
	@Autowired
	private ConfigService configService;
	
	
	@RequestMapping("/listPage.html")
    public ModelAndView listPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		ConfigBean queryModel=new ConfigBean();
		List<ConfigBean> configs=configService.queryList(queryModel);
		mav.setViewName("config/list");
		mav.addObject("configs", configs);
		return mav;
    }
	
	
	@ResponseBody
	@RequestMapping(value = "/update.html")
	public Result update(HttpServletRequest req, HttpServletResponse res,ConfigBean config) {
		Result result=new Result();
		try {
			if(configService.update(config)>0)
			{
				result.setCode(0);
				result.setMsg("修改成功");
			}
			else
			{
				result.setCode(1);
				result.setMsg("修改失败");
			}
			
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setCode(500);
			result.setMsg("server error");
			return result;
		}

	}
}
