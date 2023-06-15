package com.practice.growth.configurations;

import com.practice.growth.service.Oauth2UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity // Spring Security FilterChain에 등록이 된다.
@RequiredArgsConstructor
//@EnableMethodSecurity(securedEnabled = true) @secured 애노테이션 활성화, prePostEnabled = preAuthorize 애노테이션 활성화
public class SecurityConfiguration extends AdminAbstractSecurityConfiguration {

    private final Oauth2UserDetailsServiceImpl oauth2UserDetailsService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/bower_components/**", "/js/**", "/css/**", "/fonts/**", "/images/**", "/favicon.*", "/*/icon-*", "/error");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        http.addFilterBefore(characterEncodingFilter, CsrfFilter.class);

        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/secure/**").permitAll()
                .antMatchers("/system/**").hasRole("SYSTEM")
                .anyRequest().authenticated();

        http.formLogin()
            .loginPage("/secure/login")
            .loginProcessingUrl("/secure/login-process") // 해당 주소가 호출이 되면 Security가 낚아채서 로그인 Process를 진행한다.
            .successHandler(successHandler())
            .failureHandler(failureHandler())
            .permitAll();

        /**
         * 1. oauth code 받기
         * 2. accessToken 받기 (권한)
         * 3. 사용자 정보 Get
         * 4. 정보를 토대로 회원가입 OR 로그인
         */
        http.oauth2Login()
            .loginPage("/secure/login")
            .userInfoEndpoint() // oauth2 로그인 후 정보를 Get (AccessToken + User 정보)
            .userService(oauth2UserDetailsService);

        return http.build();
    }
}
