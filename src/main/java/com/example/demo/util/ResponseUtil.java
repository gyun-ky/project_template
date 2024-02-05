package com.example.demo.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.SuccessResponseDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponseUtil {

    public static <T> ResponseEntity<SuccessResponseDto<T>> createSuccessResponse() {
        SuccessResponseDto<T> dto = SuccessResponseDto.<T>builder()
                .successYn(true)
                .data(null)
                .build();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public static <T> ResponseEntity<SuccessResponseDto<T>> createSuccessResponse(HttpHeaders httpHeaders) {
        SuccessResponseDto<T> dto = SuccessResponseDto.<T>builder()
                .successYn(true)
                .data(null)
                .build();
        return new ResponseEntity<>(dto, httpHeaders, HttpStatus.OK);
    }

    public static <T> ResponseEntity<SuccessResponseDto<T>> createSuccessResponse(T data) {
        SuccessResponseDto<T> dto = SuccessResponseDto.<T>builder()
                .successYn(true)
                .data(data)
                .build();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public static <T> ResponseEntity<SuccessResponseDto<T>> createSuccessResponse(T data, HttpHeaders httpHeaders) {
        SuccessResponseDto<T> dto = SuccessResponseDto.<T>builder()
                .successYn(true)
                .data(data)
                .build();
        return new ResponseEntity<>(dto, httpHeaders,HttpStatus.OK);
    }

}
