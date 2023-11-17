package com.example.userservice;

import com.example.userservice.error.FeignErrorDecoder;
import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	/* RestTemplate 를 서비스 이름으로 사용하기 위해 */
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	/* Feign Package 내부에 있는 Logger */
	@Bean
	public Logger.Level feignLoggerLevel() {
		/* 전체 로그 레벨 확인 */
		return Logger.Level.FULL;
	}

//	@Bean
//	public FeignErrorDecoder getFeignErrorDecoder() {
//		return new FeignErrorDecoder();
//	}
}
