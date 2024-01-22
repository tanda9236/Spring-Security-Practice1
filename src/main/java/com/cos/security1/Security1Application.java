package com.cos.security1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Security1Application {

	public static void main(String[] args) {
		SpringApplication.run(Security1Application.class, args);
	}

	@Bean // 순환 오류로 위치 옮김
	BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
}
