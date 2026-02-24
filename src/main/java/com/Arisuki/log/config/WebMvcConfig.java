package com.Arisuki.log.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// /images/** でアクセスされたら static/images フォルダを参照
		registry.addResourceHandler("/images/**")
				.addResourceLocations("classpath:/static/images/");
	}

}
