package com.jiongzai.pay.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import com.jiongzai.pay.common.ExcelData;
import com.jiongzai.pay.model.CdkeyBean;
import com.jiongzai.pay.service.CdkeyService;
import com.jiongzai.pay.service.GroupService;
import com.jiongzai.pay.util.ResultUtil;
import com.jiongzai.pay.util.date.DateTimeUtil;
import com.jiongzai.pay.util.date.DateUtil;
import com.jiongzai.pay.util.excel.ExportExcelUtil;

@RequestMapping("/cdkey")
@Controller
public class CdkeyController {

	@Autowired
	private CdkeyService cdkeyService;

	@Autowired
	private GroupService groupService;

	@ResponseBody
	@RequestMapping(value = "/list.html")
	public Map list(HttpServletRequest req, HttpServletResponse res, int page, int limit, CdkeyBean cdk) {
		CdkeyBean queryModel = new CdkeyBean();
		if (cdk.getOrder_id() != null)
			queryModel.setOrder_id(cdk.getOrder_id());
		if (!StringUtils.isEmpty(cdk.getCard_no()))
			queryModel.setCard_no(cdk.getCard_no());
		if (cdk.getStatus() != null)
			queryModel.setStatus(cdk.getStatus());
		if (cdk.getIs_use() != null)
			queryModel.setIs_use(cdk.getIs_use());
		if (cdk.getGroup_id() != null)
			queryModel.setGroup_id(cdk.getGroup_id());
		if (cdk.getFace_value() != null)
			queryModel.setFace_value(cdk.getFace_value());
		PageInfo<CdkeyBean> pageInfo = cdkeyService.pageList(queryModel, page, limit);
		return ResultUtil.getQuerySuccessResult(pageInfo);
	}

	@RequestMapping("/listPage.html")
	public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		mav.addObject("notExportSumToday", cdkeyService.notExportSumToday());
		mav.addObject("exportSumToday", cdkeyService.exportSumToday());
		mav.addObject("notGetSumToday", cdkeyService.notGetSumToday());
		mav.setViewName("order/cdkeyList");
		return mav;
	}
	
	@RequestMapping("/exportPage.html")
	public ModelAndView exportPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		mav.setViewName("order/export");
		return mav;
	}
	

	@RequestMapping("/exportCdkey.html")
	public void exportCdkey(HttpServletRequest request, HttpServletResponse response,Integer type,Integer face_value)
			throws IOException {
		try {
			CdkeyBean t = new CdkeyBean();
			List<CdkeyBean> cdkeyBeans = null;
			if(type==1)
			{
				t.setStatus(1);
				t.setIs_use(0);
				if(face_value!=null&&face_value>0)t.setFace_value(face_value);
				cdkeyBeans = cdkeyService.getCdkList(t);
			}
			else if(type==2)
			{
				cdkeyBeans = cdkeyService.getTodayList();
			}
			else if(type==3)
			{
				cdkeyBeans = cdkeyService.getYesTodayList();
			}
			if (cdkeyBeans!=null&&cdkeyBeans.size() > 0) {
				ExcelData data = new ExcelData();
				data.setName(DateUtil.getCurrentDateStr());
				List<String> titles = new ArrayList<String>();
			    titles.add("卡号");
			    titles.add("卡密");
			    titles.add("卡面值(元)");
			    titles.add("状态");
			    titles.add("购买手机号");
			    titles.add("获取时间");
			    data.setTitles(titles);
			    List<List<Object>> rows = new ArrayList<List<Object>>();
				for (CdkeyBean cdkeyBean : cdkeyBeans) {
					List<Object> row = new ArrayList<Object>();
					 row.add(cdkeyBean.getNo());
					 row.add(cdkeyBean.getPwd());
					 row.add(cdkeyBean.getFace_value());
					 row.add(cdkeyBean.getIs_use()==0?"未使用":"已使用");
					 row.add(cdkeyBean.getCard_no());
					 row.add(DateTimeUtil.unixTime2DateStr(cdkeyBean.getCreate_time()));
					 rows.add(row);
					if(cdkeyBean.getStatus()==1)
					{
						CdkeyBean updateModel = new CdkeyBean();
						updateModel.setId(cdkeyBean.getId());
						updateModel.setExport_time(System.currentTimeMillis()/1000);
						updateModel.setStatus(2);
						cdkeyService.update(updateModel);
					}
				}
				data.setRows(rows);
				ExportExcelUtil.exportExcel(response,DateUtil.getCurrentDateStr()+".xls",data);
			}
			else
			{
				response.getOutputStream().print("<script>alert('no data')</script>");;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
