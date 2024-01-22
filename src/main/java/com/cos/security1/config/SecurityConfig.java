package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
// secured 어노테이션 활성화, preAuthozire/postAuthozire 어노테이션 활성화
public class SecurityConfig {
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
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
    			.usernameParameter("username")
    			.passwordParameter("password")
    			.loginProcessingUrl("/login") // 스프링 시큐리티가 해당 주소로 요청오는 로그인을 가로채서 대신 로그인
    			.defaultSuccessUrl("/")
    			);
    	http
    	.oauth2Login((oauth2Login) ->
    		oauth2Login
    			.loginPage("/loginForm")
    			// 구글 로그인이 완료된 뒤의 후처리 필요.
    			// Tip : 코드X, (엑세스토큰+사용자프로필정보O 한번에 받음)
    			// 1. 코드받기(인증), 2. 액세스토큰(권한), 3. 사용자프로필 정보 받기
    			// 4. 정보를 토대로 자동 회원가입, 정보가 부족하면
    			.userInfoEndpoint()
    			.userService(principalOauth2UserService)
    			);

        return http.build();
    }
}
