package com.cos.security1.config.oauth.provider;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo{

	private Map<String, Object> attributes; // oauth2User.getAttributes()
	
	public KakaoUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String getProviderId() {
		return (String)attributes.get("id").toString();
	}

	@Override
	public String getProvider() {
		return "kakao";
	}
	
	@Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }// "kakao_account" 객체 안의 "email" 키 값

    @Override
    public String getName() { 
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return (String) properties.get("nickname");
    }// "properties" 객체 안의 "nickname" 키 값

// <카카오 구조>
//	{
//	id=3297747359,
//	connected_at=2024-01-22T17:23:41Z,
//		properties=
//			{
//			nickname=슬기},
//			}
//      		kakao_account=
//    			{
//    			profile_nickname_needs_agreement=false,
//    			profile_image_needs_agreement=true,
//    			profile={nickname=슬기},
//    			has_email=true,
//    			email_needs_agreement=false,
//    			is_email_valid=true,
//    			is_email_verified=true,
//    			email=leelobo2469@gmail.com
//    			}
//    }
}
