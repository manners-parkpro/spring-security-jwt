package com.practice.growth.configurations;

import com.practice.growth.filter.JwtFilter;
import com.practice.growth.provider.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity // Spring Security FilterChain에 등록이 된다.
//@EnableMethodSecurity(securedEnabled = true) @secured 애노테이션 활성화, prePostEnabled = preAuthorize 애노테이션 활성화
public class JWTSecurityConfiguration extends AdminAbstractSecurityConfiguration {

    private final TokenProvider tokenProvider;

    public JWTSecurityConfiguration(TokenProvider tokenProvider) {
        super(tokenProvider); // super() 메소드는 부모 클래스의 생성자를 호출할 때 사용됩니다.
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/bower_components/**", "/js/**", "/css/**", "/fonts/**", "/images/**", "/favicon.*", "/*/icon-*", "/error");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        // JWT 필수 Setting
        http.addFilterBefore(characterEncodingFilter, CsrfFilter.class) // Csrf : 사용자가 자신의 의지와는 무관하게 공격자가 의도한 행위(수정, 삭제, 등록 등)를 특정 웹사이트에 요청하게 하는 공격
            .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable(); // http basic 은 Header에 ID,PW를 들고 다닌다. ( 암호화가 안되며 탈취되기 때문에 사용 X / https 는 암호화 가능 )

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilter(corsFilter()); // Security Filter에 corsFilter 등록
        // JWT 필수 Setting End.

        http.authorizeRequests()
                .antMatchers("/api/v1/account/**").hasRole("USER")
                .antMatchers("/api/v1/system/**").hasRole("SYSTEM")
                .anyRequest().permitAll();

        return http.build();
    }
}
