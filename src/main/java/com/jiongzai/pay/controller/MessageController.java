package com.jiongzai.pay.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.model.MessageBean;
import com.jiongzai.pay.service.MessageService;
import com.jiongzai.pay.util.ResultUtil;

@RequestMapping("/msg")
@Controller
public class MessageController {

	@Autowired
	private MessageService messageService;

	@ResponseBody
	@RequestMapping(value = "/list.html")
	public Map list(HttpServletRequest req, HttpServletResponse res, int page, int limit, MessageBean msg) {
		MessageBean queryModel = new MessageBean();
		if (msg.getType() != null)
			queryModel.setType(msg.getType());
		if (!StringUtils.isEmpty(msg.getPhone()))
			queryModel.setPhone(msg.getPhone());
		if (msg.getCreate_time() != null)
			queryModel.setCreate_time(msg.getCreate_time());
		PageInfo<MessageBean> pageInfo = messageService.pageList(queryModel, page, limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}

	@RequestMapping("/listPage.html")
	public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		mav.setViewName("msg/list");
		return mav;
	}
	
}
