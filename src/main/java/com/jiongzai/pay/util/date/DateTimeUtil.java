package com.jiongzai.pay.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
	private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String getCurrentDateTimeStr()
	{
		return formatDate(new Date());
	}
	
	public static String getCurrentDateTimeyyyyMMddHHmmss()
	{
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
		return simpleDateFormat.format(new Date());
	}
	
	public static Date getCurrentDate()
	{
		String toDay=DateUtil.getCurrentDateStr()+" 00:00:00";
		return string2Date(toDay);
	}
	
	public static Long getTodayUnixTime()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long date =calendar.getTime().getTime();
        return date/1000;
	}
	
	public static Long strDate2UnixTime(String dateStr) throws ParseException
	{
	     Date date = simpleDateFormat.parse(dateStr);
	     return date.getTime()/1000;
	}
	
	public static String unixTime2DateStr(long unixTime) throws ParseException
	{
	     return simpleDateFormat.format(new Date(unixTime*1000));
	}
	
	public static String formatDate(Date date)
	{
		if(date!=null)
		{
			return simpleDateFormat.format(date);
		}
		else
		{
			return "";
		}
	}
	public static Date string2Date(String dateStr)
	{
		if(dateStr!=null&&!"".equals(dateStr))
		{
			try {
				return simpleDateFormat.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}
