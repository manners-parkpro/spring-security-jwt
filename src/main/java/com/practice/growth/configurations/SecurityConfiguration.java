package com.practice.growth.configurations;

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
//@EnableMethodSecurity(securedEnabled = true) @secured 애노테이션 활성화, prePostEnabled = preAuthorize 애노테이션 활성화
public class SecurityConfiguration extends AdminAbstractSecurityConfiguration {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/bower_components/**", "/js/**", "/css/**", "/fonts/**", "/images/**", "/favicon.*", "/*/icon-*");
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

        return http.build();
    }
}
