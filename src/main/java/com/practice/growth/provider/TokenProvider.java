package com.practice.growth.provider;

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
import java.util.Collection;
import java.util.Date;

import static com.practice.growth.utils.JwtUtils.AUTHORIZATION_HEADER;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Log4j2
@Component
public class TokenProvider implements InitializingBean {

    private final String secret;
    private Key key;

    public TokenProvider(@Value("${jwt.secret-key}") String secret) {
        this.secret = secret;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.debug("getToken.bearerToken : " + bearerToken);

        if (StringUtils.isNotBlank(bearerToken)) {
            if (bearerToken.startsWith("Bearer "))
                return bearerToken;
            else
                return "Bearer " + bearerToken;
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

    /**
     * Authentication 기반 토큰 생성 메소드.
     * {@link #generateToken(String, Collection)}
     * @param authentication
     * @return JWT(String)
     */
    public String generateToken(Authentication authentication) {
        return generateToken(authentication.getName(), authentication.getAuthorities());
    }

    /**
     * Username 및 Authorities 기반 토큰 생성 메소드.
     * @param username
     * @param authorities
     * @return JWT(String)
     */
    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", authorities.stream().findFirst().get().toString())
                .setExpiration(getExpireDate())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
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
                .get("role", String.class);

    }

    private Date getExpireDate() {
        Date now = new Date();
        return new Date(now.getTime() + JwtUtils.accessTokenValidMillisecond);
    }
}
