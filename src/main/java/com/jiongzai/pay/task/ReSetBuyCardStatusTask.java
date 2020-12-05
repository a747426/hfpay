package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.CardService;

@Component
public class ReSetBuyCardStatusTask {
	
	private Log log = LogFactory.getLog(ReSetBuyCardStatusTask.class);

	@Autowired
	private CardService cardService;
	
	@Scheduled(cron = "0 0 0 * * ?")
	public void execute() {
		try {
			log.info("重置当日黑名单任务start");
			cardService.reSetBuyCardStatus();
			log.info("重置当日黑名单任务end");
		} catch (Exception e) {
			log.error("重置当日黑名单任务异常", e);
		}
	}

}