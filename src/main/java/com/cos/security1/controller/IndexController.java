package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// 일반 로그인
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(
			Authentication authentication,
			@AuthenticationPrincipal PrincipalDetails userDetails) { //DI(의존성주입)
		System.out.println("/test/login ================");
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
		System.out.println("authentication : "+principalDetails.getUser());
		
		System.out.println("userDetails : "+userDetails.getUser());
		return "세션 정보 확인하기";
	}
	
	// Oauth 로그인
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOauthLogin(
			Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { //DI(의존성주입)
		System.out.println("/test/oauth/login ================");
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		System.out.println("authentication : "+oAuth2User.getAttributes());
		System.out.println("oAuth2User : "+oauth.getAttributes());
		
		return "Oauth 세션 정보 확인하기";
	}
	// 머스테치 기본폴더 src/main/resources/
	// 뷰리졸버 설정 : templates (prefix), .mustache (suffix) 생략가능
	@GetMapping({"","/"})
	public String index() {
		return "index"; // src/main/resources/templates/index.mustache // 알아서 경로
	}
	
	// OAuth, 일반 로그인 => PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : "+principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	@GetMapping("/loginForm") // SecurityConfig 파일 생성후 기존 로그인화면 변경
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		
		userRepository.save(user);
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN") // role하나 간단히 걸어줄 때
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	//	@PostAuthorize(~~)// 함수가 실행된 후에 (잘안씀)
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 복수 role
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
	
}
