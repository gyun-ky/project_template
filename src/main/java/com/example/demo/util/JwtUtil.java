package com.example.demo.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.demo.constant.ErrorCode;
import com.example.demo.exception.RestApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JwtUtil {
	private SecretKey secretKey;

	@Value("${auth.jwt.atkDurationMin}")
	private Long atkDurationMin;

	@Value("${auth.jwt.rtkDurationDay}")
	private Long rtkDurationDay;

	// private final long refreshExpireTime = 1 * 60 * 1000L * 60 * 24 * 14; // 14일

	public JwtUtil(String secret) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String createAccessToken(String memberId) {

		Date nowDate = new Date();

		long accessExpireTime = atkDurationMin * 60 * 1000L;

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setHeaderParam("alg", "HS256")
			.setIssuer("demo")
			.setIssuedAt(nowDate)
			.setExpiration(new Date(nowDate.getTime() + accessExpireTime))
			.setSubject("ATK")
			.claim("id", memberId)
			.signWith(secretKey)
			.compact();
	}

	public Claims getPayload(final String token) {
		return tokenToJws(token).getBody();
	}

	public String getToken() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String authorizationHeader = request.getHeader("AUTHORIZATION");
		if (authorizationHeader == null) {
			throw new RestApiException(ErrorCode.ATK_NOT_EXIST);
		}
		if (!authorizationHeader.startsWith("Bearer")) {
			throw new RestApiException(ErrorCode.ATK_NOT_EXIST);
		}
		String atk = authorizationHeader.substring(6);
		if (atk.isEmpty()) {
			throw new RestApiException(ErrorCode.ATK_NOT_EXIST);
		} else {
			return atk;
		}
	}

	public String validateAccessToken(final String token) {
		try {
			final Jws<Claims> claims = tokenToJws(token);
			if (claims.getBody().getExpiration().before(new Date())) {
				throw new RestApiException(ErrorCode.JWT_TOKEN_EXPIRED);
			}
			if (claims.getBody().get("sub").equals("ATK") && claims.getBody().get("iss").equals("demo")) {
				return claims.getBody().get("id").toString();

			} else {
				throw new RestApiException(ErrorCode.INVALID_TOKEN_ERROR);
			}
		} catch (final JwtException e) {
			throw new RestApiException(ErrorCode.INVALID_TOKEN_ERROR);
		}
	}

	private Jws<Claims> tokenToJws(final String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
		} catch (final ExpiredJwtException e) {    // JWT 유효시간 초과
			throw new RestApiException(ErrorCode.JWT_TOKEN_EXPIRED);
		} catch (final SignatureException e) {    // JWT 시그니터 검증 실패
			throw new RestApiException(ErrorCode.INVALID_TOKEN_ERROR);
		} catch (final Exception e) {
			throw new RestApiException(ErrorCode.INVALID_TOKEN_ERROR);
		}
	}

	public RefreshToken createInitialRefreshToken(String memberId) {
		String refreshToken = UUID.randomUUID().toString();
		return new RefreshToken(refreshToken, memberId, rtkDurationDay * 24 * 60 * 60);
	}

	public RefreshToken createReissuedRefreshToken(String memberId, Long ttl) {
		String refreshToken = UUID.randomUUID().toString();
		return new RefreshToken(refreshToken, memberId, ttl);
	}

	@Getter
	public static class RefreshToken {
		private final String refreshToken;
		private final String memberId;
		private final Long expireSec;

		public RefreshToken(String refreshToken, String memberId, Long expireSec) {
			this.refreshToken = refreshToken;
			this.memberId = memberId;
			this.expireSec = expireSec;
		}
	}
}
