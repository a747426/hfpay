package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.OrderService;

@Component
public class NotifyOrderOnlineTask {
	
	private Log log = LogFactory.getLog(NotifyOrderOnlineTask.class);

	@Autowired
	private OrderService orderService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 3000)
	public void execute() {
		try {
			if(NotifyOrderOnlineTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("检查线上订单状态任务start");
				NotifyOrderOnlineTask.isReturn=false;
				orderService.notifyOrderOnlineTask();
				NotifyOrderOnlineTask.isReturn=true;
				//log.info("检查线上订单状态任务end");
			}
		} catch (Exception e) {
			log.error("检查线上订单状态任务异常", e);
			NotifyOrderOnlineTask.isReturn=true;
		}
	}

}
