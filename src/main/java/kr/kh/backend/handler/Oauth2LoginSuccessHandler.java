package kr.kh.backend.handler;

import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kh.backend.domain.User;
import kr.kh.backend.dto.security.JwtToken;
import kr.kh.backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 소셜 로그인에 성공하면 jwt 토큰을 발행한다.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("oauth login success handler : {}", authentication);

        // 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        Map<String, Object> responseToken = new HashMap<>();
        responseToken.put("accessToken", jwtToken.getAccessToken());
        responseToken.put("refreshToken", jwtToken.getRefreshToken());

        // refresh token 은 쿠키 (HttpOnly) 로 전송
        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtToken.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24); // 쿠키 유효기간 1일

        // access token 은 헤더로 전송
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Authorization", "Bearer " + jwtToken.getAccessToken());
        response.addCookie(refreshTokenCookie);
    }
}
