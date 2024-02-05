package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.interceptor.AuthenticateInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final AuthenticateInterceptor authenticateInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticateInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/api/v1/auth/login", "/"); // 개발 시, postman 테스트 용이성을 위해 주석처리
			// .excludePathPatterns("/**"); // 개발 시, postman 테스트 용이성을 위해 주석해제
	}
}
