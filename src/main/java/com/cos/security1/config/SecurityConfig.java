package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
public class SecurityConfig {
	
	@Bean // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록
	BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. CSRF 해제
    	http
    	.csrf((csrfConfig) ->
    		csrfConfig.disable()); // csrf 토큰 비활성화 (테스트시 걸어두는게 좋음)
        // 2. 인증, 권한 필터 설정
        http.authorizeRequests(
                authorize -> authorize.antMatchers("/user/**").authenticated()
                        .antMatchers("/manager/**").access("hasRole('ADMIN') or hasRole('MANAGER')")
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                );
        // 3. Form 로그인 설정
    	http
    	.formLogin((formLogin) ->
    		formLogin
    			.loginPage("/loginForm")
//    			.usernameParameter("username")
//    			.passwordParameter("password")
//    			.loginProcessingUrl("/auth/loginProc") // 52 스프링 시큐리티가 해당 주소로 요청오는 로그인을 가로채서 대신 로그인
//    			.successHandler((eq, resp, authentication) -> {
//    				System.out.println("디버그 : 로그인이 완료되었습니다");
//    				resp.sendRedirect("/");
//    			})
//    			.failureHandler((req, resp, ex) -> {
//    				System.out.println("디버그 : 로그인 실패 -> " + ex.getMessage());
//    			})
    			);

        return http.build();
    }
}
