package com.example.demo.exception;

import com.example.demo.constant.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {

	private ErrorCode errorCode;

	public RestApiException(ErrorCode e) {
		super(e.getMessage());
		this.errorCode = e;
	}

}