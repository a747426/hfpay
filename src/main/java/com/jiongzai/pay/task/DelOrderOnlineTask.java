package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.OrderService;

@Component
public class DelOrderOnlineTask {
	
	private Log log = LogFactory.getLog(DelOrderOnlineTask.class);

	@Autowired
	private OrderService orderService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 60000)
	public void execute() {
		try {
			if(DelOrderOnlineTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("删除线上购卡订单状态任务start");
				DelOrderOnlineTask.isReturn=false;
				orderService.delOrderOnlineTask();
				DelOrderOnlineTask.isReturn=true;
				//log.info("删除线上购卡订单状态任务end");
			}
		} catch (Exception e) {
			log.error("删除线上超时购卡订单状态任务异常", e);
			DelOrderOnlineTask.isReturn=true;
		}
	}

}
