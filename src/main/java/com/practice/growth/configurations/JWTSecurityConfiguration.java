package com.practice.growth.configurations;

import com.practice.growth.configurations.component.GFFilterInvocationSecurityMetadataSource;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.filter.JwtAuthenticationFilter;
import com.practice.growth.filter.JwtAuthorizationFilter;
import com.practice.growth.handler.JwtAccessDeniedHandler;
import com.practice.growth.handler.JwtAuthenticationEntryPoint;
import com.practice.growth.provider.JwtProvider;
import com.practice.growth.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity // Spring Security FilterChain에 등록이 된다.
//@EnableMethodSecurity(securedEnabled = true) @secured 애노테이션 활성화, prePostEnabled = preAuthorize 애노테이션 활성화
@RequiredArgsConstructor
public class JWTSecurityConfiguration extends AdminAbstractSecurityConfiguration {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtProvider jwtProvider;

    @Override
    public FilterInvocationSecurityMetadataSource gfFilterInvocationSecurityMetadataSource() {
        return new GFFilterInvocationSecurityMetadataSource(MenuType.AdminConsole);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/bower_components/**", "/js/**", "/css/**", "/fonts/**", "/img/**", "/error");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        // JWT 필수 Setting
        http.csrf().disable() // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .formLogin().disable()
                .httpBasic().disable() // http basic 은 Header에 ID,PW를 들고 다닌다. ( 암호화가 안되며 탈취되기 때문에 사용 X / https 는 암호화 가능 )
                .headers().frameOptions().sameOrigin();

        http
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
            .and()
            .addFilterBefore(characterEncodingFilter, CsrfFilter.class); // Csrf : 사용자가 자신의 의지와는 무관하게 공격자가 의도한 행위(수정, 삭제, 등록 등)를 특정 웹사이트에 요청하게 하는 공격

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.apply(new JwtCustomFilter()); // Security Filter에 corsFilter 등록
        // JWT 필수 Setting End.

        http.authorizeRequests()
                .antMatchers("/", "/login", "/logout", "/secure/**").permitAll()
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/system/**", "/setting/**").hasRole("ADMIN")
                .anyRequest().authenticated();

        return http.build();
    }


    public class JwtCustomFilter extends AbstractHttpConfigurer<JwtCustomFilter, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

            http.addFilter(corsFilter())
                .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtProvider))
                .addFilter(new JwtAuthorizationFilter(authenticationManager, jwtProvider));
        }
    }
}
