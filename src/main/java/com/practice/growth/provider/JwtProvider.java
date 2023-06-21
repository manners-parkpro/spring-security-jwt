package com.practice.growth.provider;

import com.practice.growth.domain.dto.JwtTokenDto;
import com.practice.growth.utils.JwtUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import static com.practice.growth.utils.JwtUtils.*;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Log4j2
@Component
public class JwtProvider implements InitializingBean {

    private final String secret;
    private Key key;

    public JwtProvider(@Value("${jwt.secret-key}") String secret) {
        this.secret = secret;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.debug("resolveToken.bearerToken() : " + bearerToken);

        if (StringUtils.isNotBlank(bearerToken)) {
            if (!bearerToken.startsWith("Bearer "))
                bearerToken = BEARER_TYPE + bearerToken;

            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * Token의 유효성 검증
     * @param token
     * @return boolean
     */
    public boolean validateToken(String token) {
        try {
            log.debug("token : " + token);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성
    public JwtTokenDto generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // payload "sub": "name"
                .claim(AUTHORITIES_KEY, authorities) // payload "auth": "ROLE_USER"
                .setExpiration(getExpireDate()) // payload "exp": 1516239022 (예시)
                .signWith(key, SignatureAlgorithm.HS512) // header "alg": "HS512"
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(getRefreshExpireDate())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return JwtTokenDto.builder()
                .grantType(BEARER_TYPE)
                .username(authentication.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireDate(getExpireDate().getTime())
                .build();
    }

    /**
     * 토큰으로부터 받은 정보를 기반으로 Authentication 객체를 반환하는 메소드.
     * @param accessToken
     * @return Authentication
     */
    public Authentication getAuthentication(String accessToken) {
        return new UsernamePasswordAuthenticationToken(getUsername(accessToken), "", createAuthorityList(getRole(accessToken)));
    }

    private String getUsername(String accessToken) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }

    private String getRole(String accessToken) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(accessToken)
                .getBody()
                .get(AUTHORITIES_KEY, String.class);
    }

    private Date getExpireDate() {
        Date now = new Date();
        return new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
    }

    private Date getRefreshExpireDate() {
        Date now = new Date();
        return new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);
    }
}
