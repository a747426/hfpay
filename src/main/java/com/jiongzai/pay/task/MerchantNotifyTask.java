package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.OrderService;

@Component
public class MerchantNotifyTask {
	
	private Log log = LogFactory.getLog(MerchantNotifyTask.class);

	@Autowired
	private OrderService orderService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 10000)
	public void execute() {
		try {
			if(MerchantNotifyTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("商户订单异步回调任务start");
				MerchantNotifyTask.isReturn=false;
				orderService.MerchantNotifyTask();
				MerchantNotifyTask.isReturn=true;
				//log.info("商户订单异步回调任务end");
			}
		} catch (Exception e) {
			log.error("商户订单异步回调任务异常", e);
			MerchantNotifyTask.isReturn=true;
		}
	}

}
