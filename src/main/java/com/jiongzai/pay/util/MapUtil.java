package com.jiongzai.pay.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtilsBean;

/***
 * Map工具类
 * @author xiejiong
 *
 */
public class MapUtil {
	/***
	 * Map根据key排序
	 * @param oriMap
	 * 
	 * @return
	 */
	public static Map<String, String> sortMapByKey(Map<String, String> oriMap) {  
	    if (oriMap == null || oriMap.isEmpty()) {  
	        return null;  
	    }  
	    
	    Map<String, String> map = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 升序排序
                        return obj1.compareTo(obj2);
                    }
                });
	    
	    map.putAll(oriMap);  
	    return map;  
	} 
	
	/***
	 * bean转Map
	 * @param obj
	 * @return
	 */
	public static Map<String, String> beanToMap(Object obj) { 
        Map<String, String> params = new HashMap<String, String>(0); 
        try { 
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean(); 
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj); 
            for (int i = 0; i < descriptors.length; i++) { 
                String name = descriptors[i].getName(); 
                if (!"class".equals(name)) { 
                	String value=String.valueOf(propertyUtilsBean.getNestedProperty(obj, name));
                	if("null".equals(value))
                		value="";
                    params.put(name, value); 
                } 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return params; 
	}
	
	 public static void mapToBean(Map<String, String> map, Object obj) {  
		  
	        try {  
	            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
	            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
	  
	            for (PropertyDescriptor property : propertyDescriptors) {  
	                String key = property.getName();  
	  
	                if (map.containsKey(key)) {  
	                    Object value = map.get(key); 
	                    Class s = property.getPropertyType();
	                   // System.out.println("类型转换：属性名--"+property.getName()+",属性类型--"+s.toString());
	                    // 得到property对应的setter方法  
	                    String type=s.toString();
	                    Method setter = property.getWriteMethod();  
	                    if("int".equals(type)||"long".equals(type))
	                    	setter.invoke(obj, Integer.parseInt(value.toString()));
	                    else
	                    	setter.invoke(obj, value);
	                }  
	  
	            }  
	  
	        } catch (Exception e) {  
	        	e.printStackTrace();
	            System.out.println("transMap2Bean Error " + e);  
	        }  
	  
	    } 
}
