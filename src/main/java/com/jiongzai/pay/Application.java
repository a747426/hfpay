package com.jiongzai.pay;

import java.util.Properties;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.github.pagehelper.PageHelper;
import com.jiongzai.pay.interceptor.AuthInterceptor;

@SpringBootApplication
@ComponentScan
public class Application extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// 注意这里要指向原先用main方法执行的Application启动类
		return builder.sources(Application.class);
	}

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		// 文件最大
		factory.setMaxFileSize("50MB");
		/// 设置总上传数据总大小
		factory.setMaxRequestSize("50MB");
		return factory.createMultipartConfig();
	}

	@Bean
	public PageHelper pageHelper() {
		PageHelper pageHelper = new PageHelper();
		Properties properties = new Properties();
		properties.setProperty("offsetAsPageNum", "true");
		properties.setProperty("rowBoundsWithCount", "true");
		properties.setProperty("reasonable", "true");
		properties.setProperty("dialect", "mysql"); // 配置mysql数据库的方言
		pageHelper.setProperties(properties);
		return pageHelper;
	}


	@Configuration
	static class WebMvcConfigurer extends WebMvcConfigurerAdapter {

		@Bean
		AuthInterceptor AuthInterceptor() {
			return new AuthInterceptor();
		}
		
		// 增加拦截器
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(AuthInterceptor()) // 指定拦截器类
					.addPathPatterns("/**").excludePathPatterns("/css/*", "/js/*", "/images/*", "/fonts/*", "/doc/*");
			// 指定该类拦截的url
			super.addInterceptors(registry);
		}
	}
}
