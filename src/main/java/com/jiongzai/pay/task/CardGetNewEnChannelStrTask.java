package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.CardService;

@Component
public class CardGetNewEnChannelStrTask {
	
	private Log log = LogFactory.getLog(CardGetNewEnChannelStrTask.class);

	@Autowired
	private CardService cardService;
	
	private static boolean isReturn=true;

	//当enChannelStr失效，重新获取
	@Scheduled(fixedRate = 3000)
	public void execute() {
		try {
			if(CardGetNewEnChannelStrTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("获取最新enChannelStr任务start");
				CardGetNewEnChannelStrTask.isReturn=false;
				cardService.cardGetEnChannelStrTask();
				CardGetNewEnChannelStrTask.isReturn=true;
				//log.info("获取最新enChannelStr任务end");
			}
		} catch (Exception e) {
			CardGetNewEnChannelStrTask.isReturn=true;
			log.error("获取最新enChannelStr任务异常", e);
		}
	}

}
