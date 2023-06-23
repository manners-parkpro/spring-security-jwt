package com.practice.growth.configurations;

import com.practice.growth.domain.types.MenuType;
import com.practice.growth.interceptor.CommonInterceptor;
import com.practice.growth.service.MenuService;
import com.practice.growth.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final SecurityService securityService;
    private final MenuService menuService;

    @Bean
    public CommonInterceptor commonInterceptor() {
        CommonInterceptor commonInterceptor = new CommonInterceptor(securityService, menuService);
        commonInterceptor.setMenuType(MenuType.AdminConsole);

        return commonInterceptor;
    }

    @Bean
    public MessageSource messageSource() {
        // For Language Message properties
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/messages/message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(10);

        return messageSource;
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(10)
                .build();

        factory.setReadTimeout(5000);
        factory.setConnectTimeout(2000);
        factory.setHttpClient(httpClient);
        return new RestTemplate(factory);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 가로채는 경로 설정 가능
        registry.addInterceptor(new CommonInterceptor(securityService, menuService))
                .addPathPatterns("/**").excludePathPatterns("/error", "/login", "/logout", "/secure/**", "/favicon.ico"); // 경로에 대해서는 Interceptor 가로채지 않을것이다.

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
