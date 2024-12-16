package kr.kh.backend.dto.oauth2;

public interface Oauth2UserInfo {

    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();

}
