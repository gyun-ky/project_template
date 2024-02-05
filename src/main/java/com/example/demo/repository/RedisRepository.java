package com.example.demo.repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.RedisRefreshTokenDataDto;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

	private final RedisTemplate redisTemplate;

	public void save(JwtUtil.RefreshToken refreshToken) {

		RedisRefreshTokenDataDto redisRefreshTokenDataDto = RedisRefreshTokenDataDto.builder()
			.memberId(refreshToken.getMemberId())
			.build();

		try{
			ValueOperations<String, RedisRefreshTokenDataDto> valueOperations = redisTemplate.opsForValue();
			valueOperations.set(refreshToken.getRefreshToken(), redisRefreshTokenDataDto, refreshToken.getExpireSec(),
				TimeUnit.SECONDS);
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public Optional<RedisRefreshTokenDataDto> findMemberIdByToken(String token) {
		ValueOperations<String, RedisRefreshTokenDataDto> valueOperations = redisTemplate.opsForValue();
		RedisRefreshTokenDataDto redisRefreshTokenDataDto = valueOperations.get(token);

		if (Objects.isNull(redisRefreshTokenDataDto)) {
			return Optional.empty();
		}

		return Optional.of(redisRefreshTokenDataDto);
	}

	public Long getExpireSec(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	public boolean deleteRefreshToken(String refreshToken) {
		return redisTemplate.delete(refreshToken);
	}

}
