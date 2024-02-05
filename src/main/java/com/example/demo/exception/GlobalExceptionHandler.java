package com.example.demo.exception;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demo.constant.ErrorCode;
import com.example.demo.dto.FailResponseDto;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(RestApiException.class)
	public <T> ResponseEntity<FailResponseDto> handleCustomException(final RestApiException e) {
		final ErrorCode errorCode = e.getErrorCode();
		e.printStackTrace();
		FailResponseDto dto = FailResponseDto.builder()
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.build();
		return new ResponseEntity<>(dto, errorCode.getHttpStatusCode());
	}

	@ExceptionHandler({Exception.class})
	public <T> ResponseEntity<FailResponseDto> handleAllException(final Exception e) {
		final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		e.printStackTrace();
		FailResponseDto dto = FailResponseDto.builder()
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.build();
		return new ResponseEntity<>(dto, errorCode.getHttpStatusCode());
	}
}