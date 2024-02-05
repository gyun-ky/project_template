package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UtilConfig {

	@Value("${auth.jwt.secret}")
	private String secret;

	@Bean
	public JwtUtil jwtUtil() {
		return new JwtUtil(secret);
	}

}
