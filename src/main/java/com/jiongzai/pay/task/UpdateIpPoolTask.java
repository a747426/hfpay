package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.IpPoolService;

@Component
public class UpdateIpPoolTask {
	
	private Log log = LogFactory.getLog(UpdateIpPoolTask.class);

	@Autowired
	private IpPoolService ipPoolService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 2000)
	public void execute() {
		try {
			if(UpdateIpPoolTask.isReturn)//上一次没执行完，不再执行
			{
				UpdateIpPoolTask.isReturn=false;
				ipPoolService.UpdateIpPoolTask();
				UpdateIpPoolTask.isReturn=true;
			}
		} catch (Exception e) {
			log.error("更新ip池任务异常", e);
			UpdateIpPoolTask.isReturn=true;
		}
	}

}
