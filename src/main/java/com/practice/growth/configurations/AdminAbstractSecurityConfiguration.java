package com.practice.growth.configurations;

import com.practice.growth.filter.JwtAuthenticationFilter;
import com.practice.growth.filter.JwtAuthorizationFilter;
import com.practice.growth.provider.JwtProvider;
import com.practice.growth.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Log4j2
public abstract class AdminAbstractSecurityConfiguration {

    @Bean
    public AdminAuthenticationSuccessHandlerImpl successHandler() {
        return new AdminAuthenticationSuccessHandlerImpl();
    }

    @Bean
    public AdminAuthenticationFailureHandlerImpl failureHandler() {
        return new AdminAuthenticationFailureHandlerImpl();
    }

    @Bean
    public CorsFilter corsFilter() {
        log.info("CorsFilter()");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 내 서버가 응답할 때 JSON을 자바스크립트에서 처리할 수 있게 할지 설정
        configuration.addAllowedOrigin("*"); // 모든 IP에 응답을 허용
        configuration.addAllowedHeader("*"); // 모든 Header에 응답을 허용
        configuration.addAllowedMethod("*"); // 모든 post, get, put, delete, patch 요청을 허용 하겠다.
        source.registerCorsConfiguration("/api/**", configuration);

        return new CorsFilter(source);
    }
}
