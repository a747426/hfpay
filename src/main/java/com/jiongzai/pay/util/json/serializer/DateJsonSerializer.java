package com.jiongzai.pay.util.json.serializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
/**
 * jackson转换JSON时格式化日期的标注	yyyy-MM-dd
 * @author xiejiong 2014-12-28
 *
 */
public class DateJsonSerializer extends JsonSerializer<Long> {
	private static DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	@Override
	public void serialize(Long date, JsonGenerator gen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		if(date == null){
			return ;
		}
		gen.writeString(dateFormat.format(new Date(date*1000)));
	}
	
	
}
