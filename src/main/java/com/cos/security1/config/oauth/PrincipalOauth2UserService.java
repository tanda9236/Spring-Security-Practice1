package com.cos.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.KakaoUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// from 구글 userRequest 데이터 후처리 함수
	// 함수 종료시 @Authentication 어노테이션 생성됨
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration : "+userRequest.getClientRegistration()); // registrationId로 어떤 Oauth로 로그인하는지 확인가능
		System.out.println("getAccessToken : "+userRequest.getAccessToken().getTokenValue());
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		// 구글로그인 -> 로그인창 -> 완료 -> code리턴(Oauth-Client라이브러리)
		// -> AccessToken요청 - >userRequest 정보 -> loadUser함수 호출 -> 회원프로필
		System.out.println("getAttributes : "+oauth2User.getAttributes());
		
		//
		OAuth2UserInfo oAuth2UserInfo = null;
		if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
			System.out.println("카카오 로그인 요청");
			System.out.println(oauth2User.getAttributes());
			oAuth2UserInfo = new KakaoUserInfo(oauth2User.getAttributes());
		}else {
			System.out.println("구글,네이버,카카오만 지원합니다");
		}
		
		// 회원가입진행(OAuth)
		String provider = oAuth2UserInfo.getProvider(); // google, naver, kakao
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider+"_"+providerId; //google_105055~
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) { 
			System.out.println("OAuth 최초 로그인");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		}else {
			System.out.println("OAuth 로그인 한 적 있음");
		}
		
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
