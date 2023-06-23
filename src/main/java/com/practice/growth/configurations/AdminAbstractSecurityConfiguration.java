package com.practice.growth.configurations;

import com.practice.growth.domain.types.MenuType;
import com.practice.growth.filter.JwtAuthenticationFilter;
import com.practice.growth.filter.JwtAuthorizationFilter;
import com.practice.growth.provider.JwtProvider;
import com.practice.growth.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 로그인 의사결정 방식
     * 하나만 통과해도 OK
     *
     * @return
     */
    public AffirmativeBased affirmativeBased() {
        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(new RoleVoter());
        AffirmativeBased affirmativeBased = new AffirmativeBased(accessDecisionVoters);
        affirmativeBased.setAllowIfAllAbstainDecisions(false);
        return affirmativeBased;
    }

    // =============================== 아래 Security는 학습이 필요함 ===============================
    /**
     * dynamic url설정을 위한 Interceptor
     *
     * @return
     * @throws Exception
     */
    //@Bean
    public FilterSecurityInterceptor filterSecurityInterceptor() throws Exception {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setSecurityMetadataSource(gfFilterInvocationSecurityMetadataSource());
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        try {
            filterSecurityInterceptor.afterPropertiesSet();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return filterSecurityInterceptor;
    }

    /**
     * URL 대상정보
     * @return
     */
    public abstract FilterInvocationSecurityMetadataSource gfFilterInvocationSecurityMetadataSource();
}
