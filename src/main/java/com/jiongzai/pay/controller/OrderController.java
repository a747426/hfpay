package com.jiongzai.pay.controller;

import java.util.List;
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
import com.jiongzai.pay.common.Result;
import com.jiongzai.pay.model.OrderBean;
import com.jiongzai.pay.model.OrderPoolBean;
import com.jiongzai.pay.service.GroupService;
import com.jiongzai.pay.service.OrderPoolService;
import com.jiongzai.pay.service.OrderService;
import com.jiongzai.pay.util.ResultUtil;

@RequestMapping("/order")
@Controller
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderPoolService orderPoolService;
	
	@Autowired
	private GroupService groupService;
	
	@RequestMapping("/dayFinanceListPage.html")
    public ModelAndView dayFinanceListPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.setViewName("order/dayFinanceList");
		return mav;
    }
	
	@ResponseBody
	@RequestMapping(value = "/dayFinanceList.html")
	public Map dayFinanceList(HttpServletRequest req, HttpServletResponse res,int page,int limit,OrderBean order,String timeStr) {
		PageInfo<OrderBean> pageInfo=orderService.pageDayFinanceList(order, page, limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}
	
	@ResponseBody
	@RequestMapping(value = "/list.html")
	public Map list(HttpServletRequest req, HttpServletResponse res,int page,int limit,OrderBean order) {
		OrderBean queryModel=new OrderBean();
		if(order.getId()!=null)queryModel.setId(order.getId());
		if(order.getMerchant_id()!=null)queryModel.setMerchant_id(order.getMerchant_id());
		if(order.getAmount()!=null)queryModel.setAmount(order.getAmount());
		if(!StringUtils.isEmpty(order.getPay_type()))queryModel.setPay_type(order.getPay_type());
		if(!StringUtils.isEmpty(order.getCard_no()))queryModel.setCard_no(order.getCard_no());
		if(!StringUtils.isEmpty(order.getMerchant_order_no()))queryModel.setMerchant_order_no(order.getMerchant_order_no());
		if(!StringUtils.isEmpty(order.getUnicom_order_id()))queryModel.setUnicom_order_id(order.getUnicom_order_id());
		if(order.getStatus()!=null)queryModel.setStatus(order.getStatus());
		if(order.getNotify_status()!=null)queryModel.setNotify_status(order.getNotify_status());
		PageInfo<OrderBean> pageInfo=orderService.pageList(queryModel, page, limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}
	
	
	@RequestMapping("/listPage.html")
    public ModelAndView listPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		int w_20c=0,w_30c=0,w_50c=0,w_100c=0,w_200c=0,w_300c=0,a_20c=0,a_30c=0,a_50c=0,a_100c=0,a_200c=0,a_300c=0;
		List<OrderPoolBean> leftCList=orderPoolService.getLeftOrderCount();
		for (OrderPoolBean op : leftCList) {
			if(op.getAmount()==20)
			{
				if("wechat".equals(op.getPay_type()))w_20c=op.getOrder_count();
				else a_20c=op.getOrder_count();
			}
			else if(op.getAmount()==30)
			{
				if("wechat".equals(op.getPay_type()))w_30c=op.getOrder_count();
				else a_30c=op.getOrder_count();
			}
			else if(op.getAmount()==50)
			{
				if("wechat".equals(op.getPay_type()))w_50c=op.getOrder_count();
				else a_50c=op.getOrder_count();
			}
			else if(op.getAmount()==100)
			{
				if("wechat".equals(op.getPay_type()))w_100c=op.getOrder_count();
				else a_100c=op.getOrder_count();
			}
			else if(op.getAmount()==200)
			{
				if("wechat".equals(op.getPay_type()))w_200c=op.getOrder_count();
				else a_200c=op.getOrder_count();
			}
			else if(op.getAmount()==300)
			{
				if("wechat".equals(op.getPay_type()))w_300c=op.getOrder_count();
				else a_300c=op.getOrder_count();
			}
		}
		mav.addObject("w_20c", w_20c);
		mav.addObject("w_30c", w_30c);
		mav.addObject("w_50c", w_50c);
		mav.addObject("w_100c", w_100c);
		mav.addObject("w_200c", w_200c);
		mav.addObject("w_300c", w_300c);
		mav.addObject("a_20c", a_20c);
		mav.addObject("a_30c", a_30c);
		mav.addObject("a_50c", a_50c);
		mav.addObject("a_100c", a_100c);
		mav.addObject("a_200c", a_200c);
		mav.addObject("a_300c", a_300c);
		mav.setViewName("order/list");
		return mav;
    }
	
	@ResponseBody
	@RequestMapping(value = "/notGetCdkList.html")
	public Map notGetCdkList(HttpServletRequest req, HttpServletResponse res,int page,int limit,OrderBean order) {
		OrderBean queryModel=new OrderBean();
		if(order.getId()!=null)queryModel.setId(order.getId());
		if(order.getGroup_id()!=null)queryModel.setGroup_id(order.getGroup_id());
		if(order.getAmount()!=null)queryModel.setAmount(order.getAmount());
		if(!StringUtils.isEmpty(order.getPay_type()))queryModel.setPay_type(order.getPay_type());
		if(!StringUtils.isEmpty(order.getCard_no()))queryModel.setCard_no(order.getCard_no());
		PageInfo<OrderBean> pageInfo=orderService.notGetCdkPageList(queryModel, page, limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}
	
	@ResponseBody
	@RequestMapping(value = "/reGet.html")
	public Result reGet(HttpServletRequest req, HttpServletResponse res,Long id) {
		Result result=new Result();
		try {
			OrderBean updateModel=new OrderBean();
			updateModel.setId(id);
			updateModel.setGet_cdk_status(0);
			orderService.update(updateModel);
			result.setCode(0);
			result.setMsg("操作成功");
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
	@RequestMapping(value = "/allNotify.html")
	public Result allNotify(HttpServletRequest req, HttpServletResponse res,Long id) {
		Result result=new Result();
		try {
			OrderBean queryModel=new OrderBean();
			queryModel.setStatus(1);
			queryModel.setNotify_status(0);
			List<OrderBean> orders=orderService.queryList(queryModel);
			for (OrderBean o : orders) {
				orderService.payNotify(o.getId());
			}
			result.setCode(0);
			result.setMsg("操作成功,执行"+orders.size()+"条");
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
	@RequestMapping(value = "/reGetAll.html")
	public Result reGetAll(HttpServletRequest req, HttpServletResponse res) {
		Result result=new Result();
		try {
			int count=orderService.reGetAll();
			result.setCode(0);
			result.setMsg("操作成功,执行"+count+"条");
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setCode(500);
			result.setMsg("server error");
			return result;
		}

	}
	
	
	@RequestMapping("/notGetCdkListPage.html")
    public ModelAndView notGetCdkListPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.addObject("groups", groupService.queryAll());
		mav.setViewName("order/notGetCdkList");
		return mav;
    }
	
	@ResponseBody
	@RequestMapping(value = "/setCdkSms.html")
	public Result setCdkSms(HttpServletRequest req, HttpServletResponse res,Long orderId,String verCode) {
		Result result=new Result();
		if(orderId==null)
		{
			result.setCode(0);
			result.setMsg("参数异常");
			return result;
		}
		if(StringUtils.isEmpty(verCode))
		{
			result.setCode(0);
			result.setMsg("请填写卡密验证码");
			return result;
		}
		try {
			OrderBean order=new OrderBean();
			order.setGet_cdk_sms(verCode);
			order.setGet_cdk_status(-1);
			order.setSend_get_cdk_sms_time(System.currentTimeMillis()/1000);
			order.setId(orderId);
			orderService.update(order);
			result.setCode(0);
			result.setMsg("操作成功");
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
	@RequestMapping(value = "/notify.html")
	public Result callback(HttpServletRequest req, HttpServletResponse res,Long orderId) {
		Result result=new Result();
		if(orderId==null)
		{
			result.setCode(0);
			result.setMsg("参数异常");
			return result;
		}
		try {
			if(!orderService.payNotify(orderId))
			{
				result.setCode(1);
				result.setMsg("回调失败");
				return result;
			}
			result.setCode(0);
			result.setMsg("操作成功");
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setCode(500);
			result.setMsg("商户回调地址有误");
			return result;
		}

		
	}

}
