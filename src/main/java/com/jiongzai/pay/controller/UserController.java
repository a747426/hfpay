package com.jiongzai.pay.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jiongzai.pay.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.common.Result;
import com.jiongzai.pay.model.UserBean;
import com.jiongzai.pay.service.UserService;
import com.jiongzai.pay.util.security.MD5;
import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	private String default_password= "123456";
	
	@ResponseBody
	@RequestMapping("/login.html")
    public Result login(HttpSession session,HttpServletRequest request,HttpServletResponse response,ModelAndView mav,UserBean user) {
		Result result=new Result();
		if(user==null)
		{
			result.setCode(101);
			result.setMsg("参数错误");
			return result;
		}
		if(StringUtils.isNullOrEmpty(user.getAccount()))
		{
			result.setCode(102);
			result.setMsg("请输入账号");
			return result;
		}
		if(StringUtils.isNullOrEmpty(user.getPassword()))
		{
			result.setCode(103);
			result.setMsg("请输入密码");
			return result;
		}
		user.setPassword(MD5.getMD5(user.getPassword()));
		List<UserBean> users=userService.queryList(user);
		if(users==null||users.size()==0)
		{
			result.setCode(104);
			result.setMsg("用户名或密码错误");
			return result;
		}
		UserBean loginUser=users.get(0);
		if(loginUser.getStatus()==0)
		{
			result.setCode(105);
			result.setMsg("账号被 禁用");
			return result;
		}
		result.setCode(0);
		result.setMsg("登录成功");
		String ip= HttpUtil.getIpAddr(request);
		request.getSession().setAttribute("ip", ip);
		request.getSession().setAttribute("auser", loginUser);
		return result;
    }
	
	
	@ResponseBody
	@RequestMapping(value = "/list.html")
	public Map list(HttpServletRequest req, HttpServletResponse res,int page,int limit,UserBean user) 
	{
		UserBean queryModel=new UserBean();
		if(!StringUtils.isNullOrEmpty(user.getNick_name()))queryModel.setNick_name(user.getNick_name());
		if(!StringUtils.isNullOrEmpty(user.getAccount()))queryModel.setAccount(user.getAccount());
		if(user.getStatus()!=null)queryModel.setStatus(user.getStatus());
		PageInfo<UserBean> pageInfo=userService.pageList(queryModel, page, limit);
		List<UserBean> list=pageInfo.getList();
		Map map =new HashMap();
		map.put("code", 0);
		map.put("msg", "查询成功");
		map.put("count", pageInfo.getTotal());
		map.put("data", list);
		return map; 
	}
	
	@ResponseBody
	@RequestMapping("/updatePassword.html")
	public Result updatePassword(HttpServletRequest request,HttpServletResponse response,String password,String password1){
		Result result=new Result();
		if(StringUtils.isNullOrEmpty(password))
		{
			result.setCode(1);
			result.setMsg("请输入原始密码");
			return result;
		}
		if(StringUtils.isNullOrEmpty(password1))
		{
			result.setCode(1);
			result.setMsg("请输入新密码");
			return result;
		}
		if(!password1.equals(password))
		{
			result.setCode(1);
			result.setMsg("两次输入密码不一致");
			return result;
		}
		UserBean auser=(UserBean) request.getSession().getAttribute("auser");
		UserBean adminUser=new UserBean();
		adminUser.setId(auser.getId());
		adminUser.setPassword(MD5.getMD5(password));
		userService.update(adminUser);
		result.setCode(0);
		result.setMsg("修改成功");
		return result;
	}
	
	@RequestMapping("/listPage.html")
	public ModelAndView usersListPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav)
	{
		mav.setViewName("user/list");
		return mav;
	}
	
	@RequestMapping("/addPage.html")
	public ModelAndView addPage(HttpServletRequest request, HttpServletResponse response, HttpSession session,ModelAndView mav) {
		mav.setViewName("user/add");
		return mav;
	}
	
	
	@ResponseBody
	@RequestMapping(path = "/add.html", method = RequestMethod.POST)
	public Result add(HttpServletRequest request,HttpServletResponse response,ModelAndView mav,UserBean user)
	{
		Result result=new Result();
		if(user==null)
		{
			result.setCode(1);
			result.setMsg("参数错误");
			return result;
		}
		try {
			UserBean queryModel=new UserBean();
			queryModel.setAccount(user.getAccount());
			if(userService.count(queryModel)>0)
			{
				result.setCode(1);
				result.setMsg("账号已存在");
				return result;
			}
			user.setCreate_time(System.currentTimeMillis()/1000);
			user.setUpdate_time(System.currentTimeMillis()/1000);
			user.setPassword(MD5.getMD5(default_password));
			userService.insert(user);
			result.setCode(0);
			result.setMsg("添加成功");
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
			UserBean user=new UserBean();
			user.setId(id);
			user.setStatus(status==0?1:0);
			if(userService.update(user)>0)
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
	@RequestMapping("/resetPassword.html")
	public Result resetPassword(HttpServletRequest request,HttpServletResponse response,Long id){
		Result result=new Result();
		UserBean adminUser = new UserBean();
		adminUser.setId(id);
		adminUser.setPassword(MD5.getMD5(default_password));
		userService.update(adminUser);
		result.setCode(0);
		result.setMsg("重置成功");
		return result;
	}
	
	/**
	 * 修改用户信息
	 * @param request
	 * @param response
	 * @param session
	 * @param mav
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(path = "/edit.html", method = RequestMethod.POST)
	public Map<String,Object> editPage(HttpServletRequest request, HttpServletResponse response, HttpSession session,ModelAndView mav,UserBean user) {
		Map<String,Object> map = new HashMap<String,Object>();
		UserBean updateUser = new UserBean();
		updateUser.setId(user.getId());
		updateUser.setRemarks(user.getRemarks());
		updateUser.setNick_name(user.getNick_name());
		userService.update(updateUser);
		map.put("code", 0);
		map.put("msg", "修改成功");
		return map;
		
	}
	
	@RequestMapping("/editPage.html")
	public ModelAndView editPage(HttpServletRequest request,HttpServletResponse response,ModelAndView mav,Long uid)
	{	
		UserBean user=userService.getById(uid);
		mav.addObject("user", user);
		mav.setViewName("user/edit");
		return mav;
	}
	
}
