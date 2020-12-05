package com.jiongzai.pay.controller;

import java.io.InputStream;
import java.security.acl.Group;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.common.Result;
import com.jiongzai.pay.common.UnicomOrder;
import com.jiongzai.pay.config.UnicomConfig;
import com.jiongzai.pay.model.CardBean;
import com.jiongzai.pay.model.GroupBean;
import com.jiongzai.pay.model.IpPoolBean;
import com.jiongzai.pay.service.CardService;
import com.jiongzai.pay.service.ConfigService;
import com.jiongzai.pay.service.GroupService;
import com.jiongzai.pay.service.OrderPoolService;
import com.jiongzai.pay.service.OrderService;
import com.jiongzai.pay.service.impl.OrderServiceImpl;
import com.jiongzai.pay.util.ResultUtil;
import com.jiongzai.pay.util.date.DateTimeUtil;
import com.jiongzai.pay.util.date.DateUtil;
import com.jiongzai.pay.util.excel.ExcelUtil;
import com.jiongzai.pay.util.http.Http;
import com.jiongzai.pay.util.http.Http.Response;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
 
@RequestMapping("/card")
@Controller
public class CardController {
	
	private static final Logger log = LogManager.getLogger(CardController.class);
	
	@Autowired
	private CardService cardService;
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private OrderPoolService orderPoolService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private OrderService orderService;
	
	@ResponseBody
	@RequestMapping(value = "/list.html")
	public Map list(HttpServletRequest req, HttpServletResponse res,int page,int limit,CardBean card) {
		CardBean queryModel=new CardBean();
		if(card.getId()!=null)queryModel.setId(card.getId());
		if(!StringUtils.isEmpty(card.getCom()))queryModel.setCom(card.getCom());
		if(!StringUtils.isEmpty(card.getCard_no()))queryModel.setCard_no(card.getCard_no());
		if(card.getStatus()!=null)queryModel.setStatus(card.getStatus());
		if(card.getCard_status()!=null)queryModel.setCard_status(card.getCard_status());
		if(card.getWeb_login_status()!=null)queryModel.setWeb_login_status(card.getWeb_login_status());
		if(card.getGroup_id()!=null)queryModel.setGroup_id(card.getGroup_id());
		if(card.getApp_place_order_flag()!=null)queryModel.setApp_place_order_flag(card.getApp_place_order_flag());
		if(card.getWeb_place_order_flag()!=null)queryModel.setWeb_place_order_flag(card.getWeb_place_order_flag());
		PageInfo<CardBean> pageInfo=cardService.pageList(queryModel, page, limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}
	
	
	@RequestMapping("/listPage.html")
    public ModelAndView listPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.addObject("groups", groupService.queryAll());
		mav.setViewName("card/list");
		return mav;
    }
    
    @RequestMapping("/onlineOrderListPage.html")
    public ModelAndView onlineOrderListPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav,Long cardId) {
		mav.setViewName("card/onlineOrderList");
		mav.addObject("cardId",cardId);
		return mav;
    }
    
    @ResponseBody
	@RequestMapping(value = "/onlineOrderList.html")
	public Map onlineOrderList(HttpServletRequest req, HttpServletResponse res,int page,int limit,Long cardId) throws Exception {
		CardBean card=cardService.getById(cardId);
		String query_order_mode=configService.getValueByKey("query_order_mode");
		List<UnicomOrder> orderList=null;
		if("1".equals(query_order_mode))
		{
			orderList=orderService.queryOrderWeb(cardId, card.getCard_no(), card.get_web_login_cookie(), card.getProxy_ip(), card.getProxy_port());
		}
		else
		{
			//orderList=orderService.queryOrderApp(cardId, card.getCard_no(), card.get_uop_id_cookie(), card.getProxy_ip(), card.getProxy_port());
		}
		
		PageInfo<UnicomOrder> pageInfo= new PageInfo<UnicomOrder>(orderList);
		pageInfo.setPageNum(page);
		pageInfo.setPageSize(limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}
	
	@RequestMapping("/addPage.html")
    public ModelAndView addPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav) {
		mav.addObject("groups", groupService.queryAll());
		mav.setViewName("card/add");
		return mav;
    }
	
	@RequestMapping(value = "/allReLoginPage.html")
	public ModelAndView allReLoginPage(HttpServletRequest req, HttpServletResponse res,ModelAndView mav) {
		mav.addObject("groups", groupService.queryAll());
		mav.setViewName("card/allReLogin");
		return mav;
	}
	
	@ResponseBody
	@RequestMapping(value = "/allReLogin.html")
	public Result allReLogin(HttpServletRequest req, HttpServletResponse res,Long group_id) {
		Result result=new Result();
		try {
			GroupBean group=groupService.getById(group_id);
			if(group==null)
			{
				result.setCode(1);
				result.setMsg("分组不存在");
				return result;
			}
			if(group.getStatus()!=1)
			{
				result.setCode(1);
				result.setMsg("分组被禁用，请先启用分组");
				return result;
			}
			cardService.appAllReLogin(group_id);
			cardService.webAllReLogin(group_id);
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
	@RequestMapping(value = "/remove.html")
	public Result remove(HttpServletRequest req, HttpServletResponse res,Long id) {
		Result result=new Result();
		try {
			if(cardService.removeById(id)>0)
			{
				orderPoolService.removeByCardId(id);//把订单池数据也删掉
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
	@RequestMapping(value = "/updateStatus.html")
	public Result updateStatus(HttpServletRequest req, HttpServletResponse res,Long id,Integer status) {
		Result result=new Result();
		try {
			CardBean card=new CardBean();
			card.setId(id);
			card.setStatus(status==0?1:0);
			if(cardService.update(card)>0)
			{
				if(card.getStatus()==0)//删除订单池数据
				{
					orderPoolService.removeByCardId(id);//把订单池数据也删掉
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
	@RequestMapping(value = "/updateCardStatus.html")
	public Result updateCardStatus(HttpServletRequest req, HttpServletResponse res,Long id) {
		Result result=new Result();
		try {
			CardBean card=new CardBean();
			card.setId(id);
			card.setCard_status(0);
			card.setVer_code("");
			if(cardService.update(card)>0)
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
	@RequestMapping(value = "/updateCardStatusWeb.html")
	public Result updateCardStatusWeb(HttpServletRequest req, HttpServletResponse res,Long id) {
		Result result=new Result();
		try {
			CardBean card=new CardBean();
			card.setId(id);
			card.setWeb_ver_code("");
			card.setWeb_login_status(0);
			if(cardService.update(card)>0)
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
	@RequestMapping("/add.html")
	public Result add(HttpServletRequest request,HttpServletResponse response,ModelAndView mav,CardBean card)
	{
		Result result=new Result();
		try {
			CardBean queryModel=new CardBean();
			queryModel.setCard_no(card.getCard_no());
			if(cardService.count(queryModel)>0)
			{
				result.setCode(1);
				result.setMsg("手机号已存在，添加失败!");
				return result;
			}
			card.setCreate_time(System.currentTimeMillis()/1000);
			cardService.insert(card);
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
		mav.addObject("card", cardService.getById(id));
		mav.addObject("groups", groupService.queryAll());
		mav.setViewName("card/update");
		return mav;
	}
	
	@ResponseBody
	@RequestMapping(value = "/update.html")
	public Result update(HttpServletRequest req, HttpServletResponse res,CardBean card) {
		Result result=new Result();
		try {
			cardService.update(card);
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
	
	@ResponseBody
	@RequestMapping(value = "/importData.html")
	public Result importData(HttpServletRequest req, HttpServletResponse res, @RequestParam MultipartFile file) {
		Result result = new Result();
		try {
			InputStream inputStream = file.getInputStream();
			List<List<Cell>> list = ExcelUtil.getBankListByExcel(inputStream, file.getOriginalFilename());
			inputStream.close();
			int num=0;
			for (int i = 0; i < list.size(); i++) {
				List<Cell> lo = list.get(i);
				String com = ExcelUtil.getStringValueFromCell(lo.get(0));
				String phone = ExcelUtil.getStringValueFromCell(lo.get(1));
				String group = ExcelUtil.getStringValueFromCell(lo.get(2));
				if (StringUtils.isEmpty(com) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(group)||phone.length()!=11) {
					continue;
				}
				CardBean card = new CardBean();
				card.setCard_no(phone);
				if (cardService.count(card) > 0) {
					log.info("手机号" + card.getCard_no() + "已存在，添加失败!");
					continue;
				}
				card.setCom(com);
				if (groupService.getById(Long.parseLong(group)) == null) {
					log.info("分组" + group + "不存在，添加失败!");
					continue;
				}
				card.setGroup_id(Integer.parseInt(group));
				card.setCreate_time(System.currentTimeMillis()/1000);
				cardService.insert(card);
				num++;
			}
			result.setCode(0);
			result.setMsg("上传成功,上传"+num+"条数据");
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setCode(500);
			result.setMsg("上传失败" + e.getMessage());
			return result;
		}

	}
	
}
