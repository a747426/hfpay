package com.jiongzai.pay.util;

import java.util.HashMap;
import java.util.Map;

import com.github.pagehelper.PageInfo;

public class ResultUtil {
	
	public static Map getQuerySuccessResult(PageInfo pageInfo)
	{
		Map map =new HashMap();
		map.put("code", 0);
		map.put("msg", "查询成功");
		map.put("count", pageInfo.getTotal());
		map.put("data", pageInfo.getList());
		return map;
	}
}
