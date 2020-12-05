package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.CardService;

@Component
public class CardGetNewUopIdTask {
	
	private Log log = LogFactory.getLog(CardGetNewUopIdTask.class);

	@Autowired
	private CardService cardService;
	
	private static boolean isReturn=true;

	//当uopId失效，重新获取
	@Scheduled(fixedRate = 40000)
	public void execute() {
		try {
			if(CardGetNewUopIdTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("获取最新uopId任务start");
				CardGetNewUopIdTask.isReturn=false;
				cardService.cardGetNewUopIdTask();
				CardGetNewUopIdTask.isReturn=true;
				//log.info("获取最新uopId任务end");
			}
		} catch (Exception e) {
			CardGetNewUopIdTask.isReturn=true;
			log.error("获取最新uopId任务异常", e);
		}
	}

}
