package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.CardService;

@Component
public class SendLoginSmsTask {
	
	private Log log = LogFactory.getLog(SendLoginSmsTask.class);

	@Autowired
	private CardService cardService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 3000)
	public void execute() {
		try {
			if(SendLoginSmsTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("发送登录验证码任务start");
				SendLoginSmsTask.isReturn=false;
				cardService.sendLoginSmsTask();
				SendLoginSmsTask.isReturn=true;
				//log.info("发送登录验证码任务end");
			}
		} catch (Exception e) {
			log.error("发送登录验证码任务异常", e);
			SendLoginSmsTask.isReturn=true;
		}
	}

}
