package com.example.demo.service;

import static com.example.demo.constant.ErrorCode.*;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.RedisRefreshTokenDataDto;
import com.example.demo.dto.responseDto.LoginResDto;
import com.example.demo.dto.responseDto.ReissueTokenResDto;
import com.example.demo.exception.RestApiException;
import com.example.demo.repository.RedisRepository;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtUtil jwtUtil;
	private final RedisRepository redisRepository;

	@Transactional
	public LoginResDto login(HttpServletResponse response) {

		String accessToken = jwtUtil.createAccessToken("key값");
		JwtUtil.RefreshToken refreshTokenData = jwtUtil.createInitialRefreshToken("key값");

		redisRepository.save(refreshTokenData);

		String refreshToken = refreshTokenData.getRefreshToken();

		ResponseCookie cookie = ResponseCookie
			.from("rtk", refreshToken)
			.httpOnly(true)
			// .secure(true) // TODO - 운영 반영시, 활성화 필요 (SSL 연결에서만)
			.sameSite("None")
			.path("/api/v1/auth/")
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return LoginResDto.builder()
			.accessToken(accessToken)
			.build();

	}

	public ReissueTokenResDto reissueAccessRefreshToken(HttpServletResponse response, String refreshToken){

		RedisRefreshTokenDataDto redisRefreshTokenData = redisRepository.findMemberIdByToken(refreshToken)
			.orElseThrow(() -> new RestApiException(INVALID_TOKEN_ERROR));

		// Access Token 발급
		String newAccessToken = jwtUtil.createAccessToken(redisRefreshTokenData.memberId());

		// Refresh Token 재발급
		Long refreshTokenTTL = redisRepository.getExpireSec(refreshToken);
		if(refreshTokenTTL <= 0) {
			throw new RestApiException(JWT_TOKEN_EXPIRED);
		}

		String newRefreshToken = jwtUtil.createReissuedRefreshToken(redisRefreshTokenData.memberId(),
			refreshTokenTTL).getRefreshToken();

		// 기존 Refresh Token 삭제
		redisRepository.deleteRefreshToken(refreshToken);

		ResponseCookie cookie = ResponseCookie
			.from("rtk", newRefreshToken)
			.httpOnly(true)
			// .secure(true) // TODO - 운영 반영 시, 활성화 필요 (SSL 연결에서만)
			.sameSite("None")
			.path("/api/v1/auth/")
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ReissueTokenResDto.builder()
			.accessToken(newAccessToken)
			.build();
	}

	public boolean logout(String refreshToken, String memberId) {
		RedisRefreshTokenDataDto redisRefreshTokenDataDto = redisRepository.findMemberIdByToken(refreshToken)
			.orElseThrow(() -> new RestApiException(INVALID_TOKEN_ERROR));

		if (!redisRefreshTokenDataDto.memberId().equals(memberId)) {
			throw new RestApiException(INVALID_TOKEN_ERROR);
		}

		boolean successOrNot = redisRepository.deleteRefreshToken(refreshToken);

		if (!successOrNot) {
			throw new RestApiException(ALREADY_LOG_OUT);
		}

		return true;
	}
}
