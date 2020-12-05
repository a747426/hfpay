package com.jiongzai.pay.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.common.Result;
import com.jiongzai.pay.model.MerchantBean;
import com.jiongzai.pay.service.MerchantService;
import com.jiongzai.pay.util.ResultUtil;
import com.jiongzai.pay.util.security.MD5;

@Controller
@RequestMapping("/merchant")
public class MerchantController {
	
	@Autowired
	private MerchantService merchantService;
	
	@ResponseBody
	@RequestMapping(value = "/list.html")
	public Map list(HttpServletRequest req, HttpServletResponse res,int page,int limit,MerchantBean user) {
		MerchantBean queryModel=new MerchantBean();
		if(user.getId()!=null)queryModel.setId(user.getId());
		if(user.getStatus()!=null)queryModel.setStatus(user.getStatus());
		PageInfo<MerchantBean> pageInfo=merchantService.pageList(queryModel, page, limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}
	
	@RequestMapping("/listPage.html")
    public ModelAndView listPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.setViewName("merchant/list");
		return mav;
    }
	
	@RequestMapping("/addPage.html")
	public ModelAndView addPage(HttpServletRequest request, HttpServletResponse response,ModelAndView mav) {
		mav.setViewName("merchant/add");
		return mav;
	}
	
	@RequestMapping("/updatePage.html")
	public ModelAndView updatePage(HttpServletRequest request, HttpServletResponse response,ModelAndView mav,Long mid) {
		MerchantBean merchant=merchantService.getById(mid);
		mav.setViewName("merchant/update");
		mav.addObject("merchant", merchant);
		return mav;
	}

	
	@ResponseBody
	@RequestMapping("/add.html")
	public Result add(HttpServletRequest request,HttpServletResponse response,ModelAndView mav,MerchantBean user)
	{
		Result result=new Result();
		if(user==null)
		{
			result.setCode(1);
			result.setMsg("参数错误");
			return result;
		}
		try {
			user.setCreate_time(System.currentTimeMillis()/1000);
			user.setPay_key(MD5.getMD5(System.currentTimeMillis()/1000+""));
			merchantService.insert(user);
			result.setCode(0);
			result.setMsg("添加成功");
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setCode(1);
			result.setMsg("server error");
			return result;
		}
		
	}
	
	@ResponseBody
	@RequestMapping(value = "/updateStatus.html")
	public Result updateStatus(HttpServletRequest req, HttpServletResponse res,Long id,Integer status) {
		Result result=new Result();
		try {
			MerchantBean user=new MerchantBean();
			user.setId(id);
			user.setStatus(status==0?1:0);
			if(merchantService.update(user)>0)
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
	
	@ResponseBody
	@RequestMapping(value = "/update.html")
	public Result update(HttpServletRequest req, HttpServletResponse res,MerchantBean merchant) {
		Result result=new Result();
		try {
			if(merchantService.update(merchant)>0)
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
