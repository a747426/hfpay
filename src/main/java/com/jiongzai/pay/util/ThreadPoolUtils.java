package com.jiongzai.pay.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadPoolUtils {


	private static ScheduledThreadPoolExecutor getOrderPool = new ScheduledThreadPoolExecutor(200);
	
	private static ScheduledThreadPoolExecutor getNewEnChannelStrPool = new ScheduledThreadPoolExecutor(200);
	
	private static ScheduledThreadPoolExecutor getOrderRePayPool = new ScheduledThreadPoolExecutor(200);
	
	private static ScheduledThreadPoolExecutor getCdkPool = new ScheduledThreadPoolExecutor(200);
	
	private static ScheduledThreadPoolExecutor checkOrderOnlinePool = new ScheduledThreadPoolExecutor(500);
	
	private static ScheduledThreadPoolExecutor delOrderOnlinePool = new ScheduledThreadPoolExecutor(200);
	
	private static ScheduledThreadPoolExecutor delChargeOrderOnlinePool = new ScheduledThreadPoolExecutor(100);
	
	private static ScheduledThreadPoolExecutor sendGetCdkSmsPool = new ScheduledThreadPoolExecutor(200);
	
	private static ScheduledThreadPoolExecutor merchantNotifyPool = new ScheduledThreadPoolExecutor(200);
	
	private static ScheduledThreadPoolExecutor getNewUopIdPool = new ScheduledThreadPoolExecutor(200);
	
	private static ScheduledThreadPoolExecutor sendLoginSmsPool = new ScheduledThreadPoolExecutor(200);
	
	private static ScheduledThreadPoolExecutor cardSmsLoginPool = new ScheduledThreadPoolExecutor(200);
	
	public static ScheduledThreadPoolExecutor getCardSmsLoginPool() {
		return cardSmsLoginPool;
	}
	
	public static ScheduledThreadPoolExecutor getSendLoginSmsPool() {
		return sendLoginSmsPool;
	}
	
	public static ScheduledThreadPoolExecutor getNewUopIdPool() {
		return getNewUopIdPool;
	}
	
	public static ScheduledThreadPoolExecutor getDelChargeOrderOnlinePool() {
		return delChargeOrderOnlinePool;
	}
	
	public static ScheduledThreadPoolExecutor getNewEnChannelStrPool() {
		return getNewEnChannelStrPool;
	}
	
	public static ScheduledThreadPoolExecutor getMerchantNotifyPool() {
		return merchantNotifyPool;
	}
	
	public static ScheduledThreadPoolExecutor getOrderRePayPool() {
		return getOrderRePayPool;
	}
	
	public static ScheduledThreadPoolExecutor getDelOrderOnlinePool() {
		return delOrderOnlinePool;
	}

	public static ScheduledThreadPoolExecutor getOrderPool() {
		return getOrderPool;
	}
	
	public static ScheduledThreadPoolExecutor getCdkPool() {
		return getCdkPool;
	}
	
	public static ScheduledThreadPoolExecutor getCheckOrderOnlinePool() {
		return checkOrderOnlinePool;
	}
	
	public static ScheduledThreadPoolExecutor getSendGetCdkSmsPool() {
		return sendGetCdkSmsPool;
	}
}
