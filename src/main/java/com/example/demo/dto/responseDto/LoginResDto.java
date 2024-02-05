package com.example.demo.dto.responseDto;

import lombok.Builder;

@Builder
public record LoginResDto(String accessToken) {
}
