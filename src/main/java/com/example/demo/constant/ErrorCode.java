package com.example.demo.constant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 400 - Bad Request 관련 에러 코드
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, 100, "Invalid parameter included"),
	COOKIE_REQUIRED(HttpStatus.BAD_REQUEST, 101, "쿠키가 존재하지 않습니다."),

	// 401 - Unauthorized 관련 에러 코드
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 200, "만료된 토큰입니다."),
	INVALID_TOKEN_ERROR(HttpStatus.UNAUTHORIZED , 201, "유효하지 않은 토큰입니다."),
	JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED , 202, "기한이 만료된 토큰입니다."),
	ATK_NOT_EXIST(HttpStatus.UNAUTHORIZED, 203, "Access 토큰이 존재하지 않습니다."),
	ALREADY_LOG_OUT(HttpStatus.UNAUTHORIZED, 204, "이미 로그아웃된 사용자입니다."),
	MEMBER_NOT_EXIST(HttpStatus.UNAUTHORIZED, 205, "존재하지 않는 센터원입니다."),
	RTK_NOT_EXIST(HttpStatus.UNAUTHORIZED, 206, "Refresh 토큰이 존재하지 않습니다."),

	// 500 - Internal Server Error
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 300, "Internal server error");

	private final HttpStatus httpStatusCode;
	private final Integer code;
	private final String message;

}