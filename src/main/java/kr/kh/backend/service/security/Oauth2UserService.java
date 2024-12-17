package kr.kh.backend.service.security;

import kr.kh.backend.domain.User;
import kr.kh.backend.dto.oauth2.*;
import kr.kh.backend.dto.security.LoginDTO;
import kr.kh.backend.exception.CustomException;
import kr.kh.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;

//    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
//    private String naverClientId;
//
//    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
//    private String naverClientSecret;

    @Value("${github.client-id}")
    private String githubClientId;

    @Value("${github.client-secret}")
    private String githubClientSecret;

    /**
     * 네이버 로그인
     * 로그인한 사용자 정보가 디비에 있는지 확인 후 유저 정보 리턴.
     */
//    public Authentication getNaverUser(String code, String state) {
//        log.info("네이버 로그인 서비스");
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 리액트에서 받은 인가 코드로 접근 코드 획득
//        String getTokenUrl = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code"
//                + "&client_id=" + naverClientId + "&client_secret=" + naverClientSecret
//                + "&code=" + code + "&state=" + state;
//        Map<String, String> naverToken = restTemplate.getForObject(getTokenUrl, Map.class);
//        String naverAccessToken = naverToken.get("access_token");
//
//        // 접근 코드로 사용자의 로그인 정보 획득
//        String getUserUrl = "https://openapi.naver.com/v1/nid/me";
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + naverAccessToken);
//        HttpEntity<String> request = new HttpEntity<>(null, headers);
//        ResponseEntity<Map> naverResponse = restTemplate.exchange(getUserUrl, HttpMethod.GET, request, Map.class);
//        Map<String, String> naverUser = (Map) naverResponse.getBody().get("response");
//
//        // 네이버 사용자 정보가 디비에 있는지 확인
//        if(naverUser != null) {
//            String username = naverUser.get("nickname");
//            String email = naverUser.get("email");
//            String role = "USER";
//
//            User user = userMapper.findByEmail(email);
//
//            if(user == null) {
//                user = User.builder()
//                        .nickname(username)
//                        .email(email)
//                        .platform("naver")
//                        .roles(role)
//                        .build();
//                userMapper.insertOauthUser(user);
//                log.info("새로운 oauth2 유저를 등록했습니다 : {}", user);
//            }
//
//            if (user != null && user.getPlatform() == null) {
//                log.info("이미 등록된 이메일입니다.");
//                throw new CustomException("이미 등록된 이메일입니다.", "ERROR CODE 401", HttpStatus.UNAUTHORIZED);
//            }
//
//            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            log.info("oauth2 유저가 인증되었습니다 = {}", authentication);
//
//            return authentication;
//
//        } else {
//            log.info("네이버 사용자의 정보를 확인할 수 없습니다.");
//            return null;
//        }
//    }

    /**
     * 깃허브 로그인
     */
    public Authentication getGithubUser(String code) {
        log.info("깃허브 로그인 서비스");
        RestTemplate restTemplate = new RestTemplate();

        // 응답으로 받은 코드로 access token 요청
        // 요청 객체 생성
        URI uri = UriComponentsBuilder
                .fromUriString("https://github.com/login/oauth/access_token")
                .queryParam("client_id", githubClientId)
                .queryParam("client_secret", githubClientSecret)
                .queryParam("code", code)
                .encode()
                .build().toUri();
        RequestEntity<Void> requestEntity =
                RequestEntity.post(uri)
                        .header("Accept", "application/json").build();
        // restTemplate 으로 POST 요청하여 access token 을 얻는다.
        ResponseEntity<GithubLoginDTO> response =
                restTemplate.exchange(requestEntity, GithubLoginDTO.class);
        String githubAccessToken = response.getBody().getAccess_token();

        // access token 을 사용하여 사용자의 로그인 정보 획득
        String getUserUrl = "https://api.github.com/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubAccessToken);
        HttpEntity<String> requestUser = new HttpEntity<>(null, headers);
        ResponseEntity<GithubUserDTO> githubResponse = restTemplate.exchange(getUserUrl, HttpMethod.GET, requestUser, GithubUserDTO.class);
        GithubUserDTO githubUser = githubResponse.getBody();

        // 깃허브는 이메일 정보는 따로 요청해야 한다...
        String getEmailUrl = "https://api.github.com/user/emails";
        HttpEntity<String> requestEmail = new HttpEntity<>(null, headers);
        ResponseEntity<List<GithubEmailDTO>> emailResponse = restTemplate.exchange(getEmailUrl, HttpMethod.GET, requestEmail, new ParameterizedTypeReference<List<GithubEmailDTO>>() {});
        List<GithubEmailDTO> emails = emailResponse.getBody();
        String primaryEmail = emails.isEmpty() ? "이메일 없음" : emails.get(0).getEmail();

        // 깃허브 사용자 정보가 디비에 있는지 확인
        if(githubUser != null) {
            String username = githubUser.getName();
            String email = primaryEmail;
            String role = "USER";

            User user = userMapper.findByEmail(email);
            if(user == null) {
                user = User.builder()
                        .nickname(username)
                        .email(email)
                        .platform("github")
                        .roles(role)
                        .build();
                userMapper.insertOauthUser(user);
                log.info("새로운 oauth2 유저를 등록했습니다 : {}", user);
            }

            if (user != null && user.getPlatform() == null) {
                log.info("이미 등록된 이메일입니다.");
                throw new CustomException("이미 등록된 이메일입니다.", "ERROR CODE 401", HttpStatus.UNAUTHORIZED);
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("oauth2 유저가 인증되었습니다 = {}", authentication);

            return authentication;
        } else {
            log.info("깃허브 사용자의 정보를 확인할 수 없습니다.");
            return null;
        }
    }

    /**
     * 소셜 로그인한 정보로 이미 디비에 있는 유저인지 확인한 후 로그인 진행.
     * (이 과정에서 UserDetails 객체를 생성하여 Authentication 객체 안에 넣는다.)
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("oauth2 login = {}", userRequest.getClientRegistration().getRegistrationId());

        // userRequest 정보로 loadUser 메서드를 실행해서 소셜 프로필을 가져온다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 소셜 로그인을 하면 사용자 정보가 JSON 형태로 담겨오는데 getAttributes() 메서드로 꺼낼 수 있다.
        // 자바에서 JSON 형식은 Map으로 저장하면 된다.
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 여러 개의 oauth2 제공자가 있으므로 분기 처리를 해준다.
        String userNameAttributesName = userRequest.getClientRegistration().getRegistrationId();
        Oauth2UserInfo oauth2UserInfo = null;

        if(userNameAttributesName.equals("naver")) {
            log.info("네이버 로그인 요청");
            oauth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }

        String provider = oauth2UserInfo.getProvider();
        String providerId = oauth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String email = oauth2UserInfo.getEmail();
        String role = "USER";

        // 해당 유저가 디비에 있는지 확인
        User user = userMapper.findByUsername(username);

        if(user == null) {
            log.info("등록되지 않은 oauth 사용자 입니다. 디비에 사용자 정보를 저장합니다.");
            user = User.builder()
                    .email(email)
                    .nickname(username)
                    .platform(provider)
                    .roles(role)
                    .build();
            userMapper.insertOauthUser(user);
        } else {
            log.info("이미 등록된 oauth 사용자 입니다.");
        }

        // UserDetails를 Authentication 객체로 변환 후 SecurityContextHolder 에 저장
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("oauth2 유저가 인증되었습니다 = {}", authentication);

        return user;
    }

}
