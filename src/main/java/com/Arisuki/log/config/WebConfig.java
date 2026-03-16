package com.Arisuki.log.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
            .addPathPatterns("/**") // 全てのURLを対象にする
            .excludePathPatterns(
                "/login",      // ログイン画面は除外
                "/signup",     // 新規登録画面は除外
                "/css/**",     // CSSファイル
                "/js/**",      // JSファイル
                "/images/**",  // 画像ファイル
                "/webjars/**"  // Bootstrap等
            );
    }
}