package com.Arisuki.log.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 既存の static/images
		registry.addResourceHandler("/images/**")
				.addResourceLocations("classpath:/static/images/");

		// アップロード画像用
		registry.addResourceHandler("/uploads/images/**")
				.addResourceLocations("file:uploads/images/"); // 実際のアップロード先ディレクトリ
	}

}
