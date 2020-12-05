package com.jiongzai.pay.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil {
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static String formatDate(Date date) {
		if (date != null) {
			return simpleDateFormat.format(date);
		} else {
			return "";
		}
	}
	
	public static Long strDate2UnixTime(String dateStr) throws ParseException
	{
	     Date date = simpleDateFormat.parse(dateStr);
	     return date.getTime()/1000;
	}

	public static String getCurrentDateStr() {
		return simpleDateFormat.format(new Date());
	}

	public static Date string2Date(String dateStr) {
		if (dateStr != null && !"".equals(dateStr)) {
			try {
				return simpleDateFormat.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public static String getYesterdayStr() {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, -1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		String dateString = simpleDateFormat.format(date);
		return dateString;
	}
	
	public static String getMonthFirstDayStr() {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		String dateString = simpleDateFormat.format(date);
		return dateString;
	}
	
	public static String getTomorrowStr() {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		String dateString = simpleDateFormat.format(date);
		return dateString;
	}

	// 获取日期中的月份
	public static int getMonthOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}

	// 获取日期中的天
	public static int getDayOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	// 获取两个日期之间所有的天
	public static List<Date> findDates(Date dBegin, Date dEnd) {
		List<Date> lDate = new ArrayList<Date>();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		return lDate;
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(DateUtil.getMonthFirstDayStr());
	}

	// 获取年月值
	public static int getYearAndMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		return year * 100 + month;
	}
	
	public static int getYearOfDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		return year;
	}

	public static List<Date> generateDateRange(String dateFirst, String dateSecond) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		List<Date> list = new ArrayList<Date>();
		try {
			Date dateOne = dateFormat.parse(dateFirst);
			Date dateTwo = dateFormat.parse(dateSecond);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateOne);
			list.add(dateOne);
			while (calendar.getTime().before(dateTwo)) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				list.add(calendar.getTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
