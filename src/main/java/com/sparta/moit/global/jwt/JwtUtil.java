package com.sparta.moit.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.moit.domain.member.entity.Member;
import com.sparta.moit.domain.member.entity.UserRoleEnum;
import com.sparta.moit.domain.member.repository.MemberRepository;
import com.sparta.moit.global.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    private final MemberRepository memberRepository;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private final long TOKEN_TIME =  60 * 60 * 1000L; // 60 minutes

    /* refresh token 유효 시간*/
    public static final long REFRESH_TOKEN_VALIDITY_MS = 14 * 24 * 60 * 60 * 1000L; // 14 days


    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.refresh.token.expire.time}")
    private long refreshTokenExpireTime;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public JwtUtil(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /* Test */
    public String createTokenForUser(Member user) {
        Date now = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(user.getEmail())
                        .claim(AUTHORIZATION_KEY, user.getRole()) // 사용자 권한
                        .setExpiration(new Date(now.getTime() + TOKEN_TIME))
                        .setIssuedAt(now)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }
    public String createRefreshTokenForUser(Member user) {
        // 리프레시 토큰 생성 (유니크 토큰 생성 방법)
        String refreshToken = UUID.randomUUID().toString();

        // 리프레시 토큰과 만료 시간을 Member 엔터티에 저장
        user = Member.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .refreshToken(refreshToken)
                .refreshTokenExpiry(new Date(System.currentTimeMillis() + refreshTokenExpireTime))
                .build();

        // 데이터베이스에 새로운 Member 엔터티 업데이트 (리프레시 토큰과 만료 시간 추가)
         memberRepository.save(user); // 주석 해제하고 memberRepository를 주입하여 사용

        return refreshToken;
    }

    public String createToken(String email, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token, HttpServletResponse res) throws IOException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            sendErrorResponse(res, HttpStatus.UNAUTHORIZED.value(), "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            return false;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
            sendErrorResponse(res, HttpStatus.UNAUTHORIZED.value(), "Expired JWT token, 만료된 JWT token 입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            sendErrorResponse(res, HttpStatus.UNAUTHORIZED.value(), "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            sendErrorResponse(res, HttpStatus.UNAUTHORIZED.value(), "JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            return false;
        }
    }

    private void sendErrorResponse(HttpServletResponse res, int statusCode, String errorMessage) throws IOException {
        res.setCharacterEncoding("utf-8");
        res.setContentType("application/json");
        res.setStatus(statusCode);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(res.getWriter(), Map.of("key","UNAUTHORIZED","StatusCode", res.getStatus(),"message", errorMessage));
    }

    /*리프레시 토큰 생성 메서드*/
    public String createRefreshToken(String email, UserRoleEnum role) {
        long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; /*14일*/
        Date now = new Date();

        return Jwts.builder()
                .setSubject(email) /*사용자 식별자값(ID)*/
                .claim(AUTHORIZATION_KEY, role) /*사용자 권한*/
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME)) /*만료 시간*/
                .setIssuedAt(now) /*발급일*/
                .signWith(key, signatureAlgorithm) /*암호화 알고리즘*/
                .compact();
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
