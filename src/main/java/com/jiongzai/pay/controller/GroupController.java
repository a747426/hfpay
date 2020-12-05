package com.jiongzai.pay.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.common.Result;
import com.jiongzai.pay.model.CardBean;
import com.jiongzai.pay.model.GroupBean;
import com.jiongzai.pay.service.CardService;
import com.jiongzai.pay.service.GroupService;
import com.jiongzai.pay.service.OrderPoolService;
import com.jiongzai.pay.util.ResultUtil;

@Controller
@RequestMapping("/group")
public class GroupController {
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private CardService cardService;
	
	@Autowired
	private OrderPoolService orderPoolService;
	
	@ResponseBody
	@RequestMapping(value = "/list.html")
	public Map groupList(HttpServletRequest req, HttpServletResponse res,int page,int limit,GroupBean group) {
		GroupBean queryModel=new GroupBean();
		if(!StringUtils.isEmpty(group.getName()))queryModel.setName(group.getName());
		if(!StringUtils.isEmpty(group.getTimeStr()))queryModel.setTimeStr(group.getTimeStr());
		if(group.getStatus()!=null)queryModel.setStatus(group.getStatus());
		if(group.getDispatch_order_status()!=null)queryModel.setDispatch_order_status(group.getDispatch_order_status());
		PageInfo<GroupBean> pageInfo=groupService.pageList(queryModel, page, limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}
	
	
	@RequestMapping("/listPage.html")
    public ModelAndView listPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.setViewName("group/list");
		return mav;
    }
	
	@ResponseBody
	@RequestMapping(value = "/cardList.html")
	public Map cardList(HttpServletRequest req, HttpServletResponse res,int page,int limit,Integer gId) {
		CardBean queryModel=new CardBean();
		queryModel.setGroup_id(gId);
		PageInfo<CardBean> pageInfo=cardService.pageList(queryModel, page, limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}
	
	@RequestMapping("/cardListPage.html")
    public ModelAndView cardListPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav,Long gId) {
		mav.setViewName("group/cardList");
		mav.addObject("gId", gId);
		return mav;
    }
	
	@RequestMapping("/addPage.html")
    public ModelAndView addPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.setViewName("group/add");
		return mav;
    }
	
	@ResponseBody
	@RequestMapping(value = "/remove.html")
	public Result remove(HttpServletRequest req, HttpServletResponse res,Long id) {
		Result result=new Result();
		try {
			if(groupService.removeById(id)>0)
			{
				cardService.removeByGroupId(id);
				orderPoolService.removeByGroupId(id);//把订单池数据也删掉
				result.setCode(0);
				result.setMsg("删除成功");
			}
			else
			{
				result.setCode(1);
				result.setMsg("删除失败");
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
	@RequestMapping(value = "/removeAllCard.html")
	public Result removeAllCard(HttpServletRequest req, HttpServletResponse res,Long id) {
		Result result=new Result();
		try {
			if(cardService.removeByGroupId(id)>0)
			{
				orderPoolService.removeByGroupId(id);//把订单池数据也删掉
				result.setCode(0);
				result.setMsg("删除成功");
			}
			else
			{
				result.setCode(1);
				result.setMsg("删除失败");
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
	@RequestMapping("/add.html")
	public Result add(HttpServletRequest request,HttpServletResponse response,ModelAndView mav,GroupBean group)
	{
		Result result=new Result();
		try {
			group.setCreate_time(System.currentTimeMillis()/1000);
			groupService.insert(group);
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
	
	
	@RequestMapping("/updatePage.html")
	public ModelAndView updatePage(HttpServletRequest request, HttpServletResponse response, HttpSession session,ModelAndView mav,Long id) {
		mav.addObject("group", groupService.getById(id));
		mav.setViewName("group/update");
		return mav;
	}
	
	@ResponseBody
	@RequestMapping(value = "/updateDispatchOrderStatus.html")
	public Result updateDispatchOrderStatus(HttpServletRequest req, HttpServletResponse res,Long id,Integer dispatch_order_status) {
		Result result=new Result();
		try {
			GroupBean group=new GroupBean();
			group.setId(id);
			group.setDispatch_order_status(dispatch_order_status==0?1:0);
			if(groupService.update(group)>0)
			{
				if(group.getDispatch_order_status()==0)//删除订单池数据
				{
					orderPoolService.removeByGroupId(id);//把订单池数据也删掉
				}
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
	@RequestMapping(value = "/updateStatus.html")
	public Result updateStatus(HttpServletRequest req, HttpServletResponse res,Long id,Integer status) {
		Result result=new Result();
		try {
			GroupBean group=new GroupBean();
			group.setId(id);
			group.setStatus(status==0?1:0);
			if(groupService.update(group)>0)
			{
				if(group.getStatus()==0)//删除订单池数据
				{
					orderPoolService.removeByGroupId(id);//把订单池数据也删掉
				}
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
	public Result update(HttpServletRequest req, HttpServletResponse res,GroupBean group) {
		Result result=new Result();
		try {
			groupService.update(group);
			result.setCode(0);
			result.setMsg("修改成功");
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
