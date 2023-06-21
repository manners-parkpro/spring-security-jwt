package com.practice.growth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.growth.domain.dto.JwtTokenDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.provider.JwtProvider;
import com.practice.growth.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.practice.growth.utils.JwtUtils.AUTHORIZATION_HEADER;
import static com.practice.growth.utils.JwtUtils.BEARER_TYPE;

/**
 * JWT 인증 Filter
 */
@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    /**
     * Spring Security UsernamePasswordAuthenticationFilter가 존재
     * /login with username, password (Method:post)
     * UsernamePasswordAuthenticationFilter 실행
     *
     * @return : Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("JwtAuthenticationFilter attemptAuthentication()");

        // request에 있는 username과 password를 파싱해서 자바 Object로 받기
        ObjectMapper om = new ObjectMapper();
        Account account = null;

        try {
            account = om.readValue(request.getInputStream(), Account.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // 유저네임패스워드 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword());

        /**
         * authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
         * loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
         * UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
         * UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
         * Authentication 객체를 만들어서 필터체인으로 리턴해준다.
         * Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
         * Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
         * 결론은 인증 프로바이더에게 알려줄 필요가 없음.
         *
         * 해당 Method는 CustomAuthenticationProvider implements AuthenticationProvider를 호출
         */
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authenticationResult) throws IOException, ServletException {
        log.info("successfulAuthentication()");

        JwtTokenDto jwtTokenDto = jwtProvider.generateToken(authenticationResult);
        if (jwtTokenDto == null)
            throw new AuthenticationCredentialsNotFoundException("AuthenticationCredentialsNotFoundException");

        log.info("JwtAuthenticationFilter : 토큰생성완료");
        log.info("Jwt Token : " + jwtTokenDto.getAccessToken());
        response.addHeader(AUTHORIZATION_HEADER, BEARER_TYPE + jwtTokenDto.getAccessToken());
    }
}
