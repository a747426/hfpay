package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.OrderService;

@Component
public class DelChargeOrderOnlineTask {
	
	private Log log = LogFactory.getLog(DelChargeOrderOnlineTask.class);

	@Autowired
	private OrderService orderService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 60000)
	public void execute() {
		try {
			if(DelChargeOrderOnlineTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("删除线上话费订单状态任务start");
				DelChargeOrderOnlineTask.isReturn=false;
				orderService.delChargeOrderOnlineTask();
				DelChargeOrderOnlineTask.isReturn=true;
				//log.info("删除线上话费订单状态任务end");
			}
		} catch (Exception e) {
			log.error("删除线上超时话费订单状态任务异常", e);
			DelChargeOrderOnlineTask.isReturn=true;
		}
	}

}