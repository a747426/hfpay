package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.OrderPoolService;

@Component
public class CardRePayOrderTask {
	
	private Log log = LogFactory.getLog(CardRePayOrderTask.class);

	@Autowired
	private OrderPoolService orderPoolService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 3000)
	public void execute() {
		try {
			if(CardRePayOrderTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("预下单任务start");
				CardRePayOrderTask.isReturn=false;
				orderPoolService.cardRePayOrderTask();
				CardRePayOrderTask.isReturn=true;
				//log.info("预下单任务end");
			}
		} catch (Exception e) {
			log.error("二次下单任务异常", e);
			CardRePayOrderTask.isReturn=true;
		}
	}

}
