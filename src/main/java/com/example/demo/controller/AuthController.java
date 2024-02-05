package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constant.ErrorCode;
import com.example.demo.dto.SuccessResponseDto;
import com.example.demo.dto.responseDto.LoginResDto;
import com.example.demo.dto.responseDto.ReissueTokenResDto;
import com.example.demo.exception.RestApiException;
import com.example.demo.service.AuthService;
import com.example.demo.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping("/login")
	public ResponseEntity<SuccessResponseDto<LoginResDto>> login(HttpServletRequest request, HttpServletResponse response) {
		LoginResDto result = authService.login(response);
		return ResponseUtil.createSuccessResponse(result);
	}

	@PostMapping("/reissue")
	public ResponseEntity<SuccessResponseDto<ReissueTokenResDto>> reissueAccessToken(HttpServletResponse response, HttpServletRequest request) throws Exception {

		String refreshToken = (String) request.getAttribute("rtk");

		if (refreshToken == null) {
			throw new RestApiException(ErrorCode.RTK_NOT_EXIST);
		}

		ReissueTokenResDto result = authService.reissueAccessRefreshToken(response,
			refreshToken);

		return ResponseUtil.createSuccessResponse(result);
	}

	@PostMapping("/logout")
	public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {

		String memberId = request.getAttribute("memberId").toString();

		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			throw new RestApiException(ErrorCode.INVALID_PARAMETER);
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("rtk")) {
				authService.logout(cookie.getValue(), memberId);
				break;
			}
		}

		ResponseCookie cookie = ResponseCookie
			.from("rtk", null)
			.httpOnly(true)
			.secure(true)
			.maxAge(0)
			.sameSite("None")
			.path("/auth/")
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseUtil.createSuccessResponse();
	}

}
