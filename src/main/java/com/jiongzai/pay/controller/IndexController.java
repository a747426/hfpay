package com.jiongzai.pay.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jiongzai.pay.model.CardBean;
import com.jiongzai.pay.model.OrderBean;
import com.jiongzai.pay.service.CardService;
import com.jiongzai.pay.service.OrderService;
import com.jiongzai.pay.util.date.DateTimeUtil;

@Controller
public class IndexController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CardService cardService;
	
	@RequestMapping("")
    public ModelAndView index(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.setViewName("index");
		return mav;
    }
	
	@RequestMapping("/index.html")
    public ModelAndView main(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.setViewName("index");
		return mav;
    }
	
	@RequestMapping("/welcome.html")
    public ModelAndView welcome(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		OrderBean queryModel=new OrderBean();
		queryModel.setStatus(1);
		mav.addObject("allOrderSum", new BigDecimal(orderService.sumMoney(queryModel)+"").toString());
		
		queryModel.setCreate_time(DateTimeUtil.getTodayUnixTime());
		mav.addObject("todayOrderSum", new BigDecimal(orderService.sumMoney(queryModel)+"").toString());
		queryModel.setPay_type("wechat");
		mav.addObject("todayOrderSumWx", new BigDecimal(orderService.sumMoney(queryModel)+"").toString());
		queryModel.setPay_type("alipay");
		mav.addObject("todayOrderSumAli", new BigDecimal(orderService.sumMoney(queryModel)+"").toString());
		
		CardBean cardModel=new CardBean();
		mav.addObject("allCardCount", cardService.count(cardModel));
		cardModel.setStatus(1);
		cardModel.setCard_status(1);
		cardModel.setWeb_login_status(1);
		mav.addObject("workCardCount", cardService.count(cardModel));
		mav.setViewName("welcome");
		mav.addObject("currentDateTime", DateTimeUtil.getCurrentDateTimeStr());
		return mav;
    }

	@RequestMapping("/loginPage.html")
    public ModelAndView mlogin(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.setViewName("login");
		return mav;
    }
	
	@RequestMapping("/updatePasswordPage.html")
    public ModelAndView updatePasswordPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.setViewName("password");
		return mav;
    }
	
	@RequestMapping("/loginOut.html")
    public ModelAndView loginOut(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		request.getSession().removeAttribute("auser");
		mav.setViewName("login");
		return mav;
    }
}
