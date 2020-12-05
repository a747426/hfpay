package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.OrderPoolService;

@Component
public class CardGetNewOrderTask {
	
	private Log log = LogFactory.getLog(CardGetNewOrderTask.class);

	@Autowired
	private OrderPoolService orderPoolService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 2000)
	public void execute() {
		try {
			if(CardGetNewOrderTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("预下单任务start");
				CardGetNewOrderTask.isReturn=false;
				orderPoolService.cardGetNewOrderTask();
				CardGetNewOrderTask.isReturn=true;
				//log.info("预下单任务end");
			}
		} catch (Exception e) {
			log.error("预下单任务异常", e);
			CardGetNewOrderTask.isReturn=true;
		}
	}

}
