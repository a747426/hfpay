package com.jiongzai.pay.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.dao.ConfigDao;
import com.jiongzai.pay.dao.IpPoolDao;
import com.jiongzai.pay.model.IpPoolBean;
import com.jiongzai.pay.service.IpPoolService;
import com.jiongzai.pay.util.date.DateTimeUtil;
import com.jiongzai.pay.util.http.Http;
import com.jiongzai.pay.util.http.Http.Response;
import com.jiongzai.pay.util.http.HttpProxy;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class IpPoolServiceImpl implements IpPoolService{
	
	private static final Logger logger = LogManager.getLogger(IpPoolServiceImpl.class);
	
	public static Map<String,Long> getIpType=new HashMap<String,Long>();
	
	@Autowired
	private IpPoolDao dao;
	
	@Autowired
	private ConfigDao configDao;

	@Override
	public int insert(IpPoolBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<IpPoolBean> queryList(IpPoolBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(IpPoolBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public IpPoolBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(IpPoolBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<IpPoolBean> pageList(IpPoolBean t, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<IpPoolBean> list = dao.queryList(t);
		PageInfo<IpPoolBean> pageInfo= new PageInfo<IpPoolBean>(list);
		return pageInfo;
	}

	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<IpPoolBean> queryAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void UpdateIpPoolTask() throws Exception {
		// TODO Auto-generated method stub
		int expiredCount=dao.removeExpired();
		logger.info("删除过期代理IP："+expiredCount);
		//查询ip池小于5时新增
		if(dao.count(null)>5)return;
		List<HttpProxy> ipList=this.getNewProxy();
		if(ipList==null||ipList.size()==0)
		{
			logger.info("未获取到代理ip");
			return;
		}
		int insertCont=0;
		for (HttpProxy httpProxy : ipList) {
			try {
				IpPoolBean insertModel=new IpPoolBean();
				insertModel.setIp(httpProxy.getHost());
				if(dao.count(insertModel)>0)continue;
				insertModel.setPort(httpProxy.getPort());
				insertModel.setExpire_time(httpProxy.getExpire_time());
				insertModel.setCity(httpProxy.getCity());
				dao.insert(insertModel);
				insertCont+=1;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
		logger.info("新增"+insertCont+"条代理IP");
	}
	
	@Override
	public List<HttpProxy> getNewProxy() {
		// TODO Auto-generated method stub
		String proxy_url=configDao.getValueByKey("proxy_url");
		if(StringUtils.isEmpty(proxy_url))
		{
			return null;
		}
		List<HttpProxy> ipList=new ArrayList<HttpProxy>();
		try {
			Response res=Http.create(proxy_url).requestType(Http.RequestType.FORM).get().send().getResponse();
			if(StringUtils.isEmpty(res.getResult()))
			{
				return null;
			}
			logger.info("获取代理IP："+res.getResult());
			JSONObject json=JSONObject.fromObject(res.getResult());
			if(json.getInt("ERRORCODE")==0||json.getInt("code")==0)
			{
				JSONArray array=json.getJSONArray("RESULT");
				for (int i=0;i<array.size();i++) {
					JSONObject ipDate=array.getJSONObject(i);
					String proxyIp=ipDate.containsKey("ip")?ipDate.getString("ip"):ipDate.optString("IP");
					int proxyPort=ipDate.containsKey("port")?Integer.parseInt(ipDate.getString("port")):ipDate.getInt("Port");
					long expire_time=(System.currentTimeMillis()/ 1000)+600;
					String city=ipDate.optString("city",null);
					HttpProxy proxy=new HttpProxy(proxyIp, proxyPort);
					proxy.setExpire_time(expire_time);
					proxy.setCity(city);
					ipList.add(proxy);
				}
				return ipList;
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public synchronized IpPoolBean getOneCanUseIp(String type) {
		// TODO Auto-generated method stub
		//防止多线程重复获取
		if(!"phone".equals(type)&&getIpType.containsKey(type)&&getIpType.get(type)>System.currentTimeMillis()/1000-2)return null;
		getIpType.put(type, System.currentTimeMillis()/1000);
		IpPoolBean ipPool=dao.getOneCanUseIp();
		if(ipPool!=null)
		{
			dao.removeById(ipPool.getId());
		}
		return ipPool;
	}

}
