package com.practice.growth.configurations;

import com.practice.growth.service.Oauth2UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity // Spring Security FilterChain에 등록이 된다.
@RequiredArgsConstructor
//@EnableMethodSecurity(securedEnabled = true) @secured 애노테이션 활성화, prePostEnabled = preAuthorize 애노테이션 활성화
public class JWTSecurityConfiguration extends AdminAbstractSecurityConfiguration {

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

        // JWT 필수 Setting
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(corsFilter()) // Security Filter에 corsFilter 등록
        .formLogin().disable()
        .httpBasic().disable();
        // JWT 필수 Setting End.

        http.authorizeRequests()
                .antMatchers("/api/v1/account/**").hasRole("USER")
                .antMatchers("/api/v1/system/**").hasRole("SYSTEM")
                .anyRequest().permitAll();

        return http.build();
    }
}
