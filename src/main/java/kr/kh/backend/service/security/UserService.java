package kr.kh.backend.service.security;

import kr.kh.backend.domain.EmailVerification;
import kr.kh.backend.dto.EmailVerificationDTO;
import kr.kh.backend.dto.security.JwtToken;
import kr.kh.backend.mapper.UserMapper;
import kr.kh.backend.domain.Token;
import kr.kh.backend.domain.TokenStatus;
import kr.kh.backend.dto.security.JwtToken;
import kr.kh.backend.mapper.TokenMapper;
import kr.kh.backend.security.jwt.JwtTokenProvider;
import kr.kh.backend.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;


import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenMapper tokenMapper;

    /**
     * 유저의 로그인 요청으로 들어온 username + password 를 기반으로 검증 진행 후 JWT 토큰 생성
     */
    public JwtToken generateJwtToken(String username, String password) {
        log.info("Generate JWT Token = username : {}", username);

//        // 사용자 정보를 로드
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//
//        // 비밀번호 비교
//        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
//            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
//        }

        // username + password 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        log.info("authenticationToken = {}", authenticationToken);

        // 해당 user 에 대한 검증 진행
        // 이 과정에서 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행된다.
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        log.info("authentication = {}", authentication);

        // 인증되면 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        log.info("jwtToken = {}", jwtToken);

        return jwtToken;
    }

    // 사용자 계정 찾기
    public String findUsernameByEmail(String email) {
        log.info("findUsernameByEmail email = {}", email);

        String username = userMapper.findUsernameByEmail(email);
        log.info("username = {}", username);

        if(username == null){
            throw new IllegalArgumentException("이메일 정보가 없습니다.");
        }
        log.info("username = {}", username);
        return username;
    }

    // 비밀번호 재설정
    public void changePassword(String username, String password) {
        String encryptedPassword = passwordEncoder.encode(password);

        userMapper.updatePassword(username, encryptedPassword);
    }

    // 비밀번호 암호화
//    private String encryptPassword(String password) {
//        return new BCryptPasswordEncoder().encode(password);
//    }
    /**
     * 로그아웃시 DB에 저장된 리프레쉬 토큰을 EXPIRED 로 변경
     */
    public void logout(String refreshToken) {
        log.info("로그아웃 : 리프레쉬 토큰 제거 !!");
        List<Token> tokens = tokenMapper.getTokenByToken(refreshToken);

        if(tokens != null) {
            Token token = tokens.get(0);
            try {
                token.setExpirationDate(new Date());
                token.setStatus(TokenStatus.EXPIRED);
                tokenMapper.updateToken(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
