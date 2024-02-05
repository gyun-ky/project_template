package com.example.demo.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import com.example.demo.constant.ErrorCode;
import com.example.demo.dto.FailResponseDto;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateInterceptor implements HandlerInterceptor {

	private final JwtUtil jwtUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {

		if (request.getMethod().equals("OPTIONS")) {
			return true;
		}

		String URI = request.getRequestURI();
		if (URI.equals("/api/v1/auth/reissue")) {
			Cookie cookie = WebUtils.getCookie(request, "rtk");
			if(cookie == null) {
				ObjectMapper objectMapper = new ObjectMapper();
				String responseDto = objectMapper.writeValueAsString(FailResponseDto.builder()
					.code(ErrorCode.COOKIE_REQUIRED.getCode())
					.message(ErrorCode.COOKIE_REQUIRED.getMessage())
					.build());
				response.setContentType("application/json");
				response.setCharacterEncoding("utf-8");
				response.setStatus(ErrorCode.COOKIE_REQUIRED.getHttpStatusCode().value());
				response.getWriter().write(responseDto);
				return false;
			}
			request.setAttribute("rtk", cookie.getValue());
			return true;
		}

		String accessToken = jwtUtil.getToken();
		String memberId = jwtUtil.validateAccessToken(accessToken);

		request.setAttribute("memberId", memberId);

		return true;
	}
}
