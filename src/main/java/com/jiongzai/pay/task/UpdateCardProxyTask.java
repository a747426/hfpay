package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.CardService;

@Component
public class UpdateCardProxyTask {
	
	private Log log = LogFactory.getLog(UpdateCardProxyTask.class);

	@Autowired
	private CardService cardService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 3000)
	public void execute() {
		try {
			if(UpdateCardProxyTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("更新代理ip任务start");
				UpdateCardProxyTask.isReturn=false;
				cardService.updateCardProxyTask();
				UpdateCardProxyTask.isReturn=true;
				//log.info("更新代理ip任务end");
			}
		} catch (Exception e) {
			log.error("更新代理ip任务异常", e);
			UpdateCardProxyTask.isReturn=true;
		}
	}

}
