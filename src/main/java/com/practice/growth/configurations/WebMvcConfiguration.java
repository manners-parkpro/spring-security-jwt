package com.practice.growth.configurations;

import com.practice.growth.interceptor.CommonInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Bean
    public MessageSource messageSource() {
        // For Language Message properties
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/messages/message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(10);

        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 가로채는 경로 설정 가능
        registry.addInterceptor(new CommonInterceptor())
                .excludePathPatterns("/secure/**", "/login", "/logout", "/error"); // 경로에 대해서는 Interceptor 가로채지 않을것이다.

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
