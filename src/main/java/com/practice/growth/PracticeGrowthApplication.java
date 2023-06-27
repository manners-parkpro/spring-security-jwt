package com.practice.growth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(
	exclude = {
			org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration.class,
			org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration.class,
			org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration.class
	}
)
public class PracticeGrowthApplication {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(PracticeGrowthApplication.class, args);
	}

}
