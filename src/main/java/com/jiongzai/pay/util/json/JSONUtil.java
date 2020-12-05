package com.jiongzai.pay.util.json;

import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;
/**
 * JSON工具
 * @author xiejiong 2014-12-29
 *
 */
public class JSONUtil {
	private static ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * 将实体对象转换成JSON格式的字符串
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj){
		String json = null;
		try {
			StringWriter writer = new StringWriter();
			JsonGenerator generator = mapper.getFactory().createGenerator(writer);
			mapper.writeValue(generator, obj);
			json = writer.toString();
			generator.close();
			writer.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * JSON格式的字符串转成实体对象
	 * @param json
	 * @param valueType
	 * @return
	 */
	public static <T> T fromJson(String json, Class<T> valueType){
		try {
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return null == json ? null : mapper.readValue(json, valueType);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * JSON格式的字符串转成实体对象
	 * @param json
	 * @param valueType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T formJson(String json, TypeReference<T> valueType){
		try {
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return (T) (null == json ? null : mapper.readValue(json, valueType));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isJson(String content) {
        try {
            JSONObject.fromObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
