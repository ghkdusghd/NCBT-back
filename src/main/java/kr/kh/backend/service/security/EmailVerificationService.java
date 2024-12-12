package kr.kh.backend.service.security;

import kr.kh.backend.domain.EmailVerification;
import kr.kh.backend.domain.User;
import kr.kh.backend.dto.EmailVerificationDTO;
import kr.kh.backend.dto.PracticeComplaintsDTO;
import kr.kh.backend.mapper.UserMapper;
import kr.kh.backend.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailVerificationService {
    private final UserMapper userMapper;
    private final MailService mailService;
    private static final int AUTH_CODE_LENGTH = 6;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    public void sendCodeToEmail(String email) {

        // 이메일 중복 확인
        if (userMapper.isEmailExisted(email)) {
            throw new IllegalArgumentException("email already exists");
        }

        String authCode = createAuthCode();
        String title = "[NCBT] 인증 번호";

        userMapper.deleteEmailVerification(email);

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .authCode(authCode)
                .expirationTime(LocalDateTime.now().plus(authCodeExpirationMillis, ChronoUnit.MILLIS))
                .build();
        userMapper.insertEmailVerification(emailVerification);

        mailService.sendEmail(email, title, "인증번호: " + authCode);
    }

    // 이메일 인증 코드 확인
    public EmailVerificationDTO verifyCode(String email, String authCode) {

        try {
        EmailVerification emailVerification = userMapper.findByVerifiedEmail(email);

        if (emailVerification == null) {
            throw new IllegalArgumentException("이메일 인증 코드가 존재하지 않습니다. 다시 요청해주세요");
        }

        boolean isExpired = emailVerification.getExpirationTime().isBefore(LocalDateTime.now());
        if (isExpired) {
            throw new IllegalArgumentException("인증 코드의 유효시간이 만료되었습니다. 다시 요청해주세요");
        }

        boolean isValid = emailVerification.getAuthCode().equals(authCode);
        if (!isValid) {
            throw new IllegalArgumentException("인증코드가 올바르지 않습니다.");
        }

        userMapper.deleteEmailVerification(email);

        return new EmailVerificationDTO(
                emailVerification.getEmail(),
                emailVerification.getAuthCode(),
                emailVerification.getExpirationTime()
        );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    // 인증 코드 생성
    private String createAuthCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < AUTH_CODE_LENGTH; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while generating auth code", e);
            throw new IllegalStateException("Failed to generate secure number", e);
        }
    }

    // 닉네임응으로 이메일 조회 후 인증번호 요청
    public void sendAuthCodeByNickname(String nickname) {
        log.info("sendAuthCodeByNickname - nickname: {}", nickname);

        User user = userMapper.findByUsername(nickname);
        if (user == null) {
            throw new IllegalArgumentException("cannot find user by nickname(username)");
        }

        String email = user.getEmail();

        String authCode = createAuthCode();
        String title = "[NCBT] 인증 번호";

        userMapper.deleteEmailVerification(email);

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .authCode(authCode)
                .expirationTime(LocalDateTime.now().plus(authCodeExpirationMillis, ChronoUnit.MILLIS))
                .build();
        userMapper.insertEmailVerification(emailVerification);

        mailService.sendEmail(email, title, "인증번호: " + authCode);
    }

    // 이메일 존재하는경우 인증번호 요청 -> 기존의 로직을 쓰면 중복확인으로 걸림
    public void sendAuthCodeByEmail(String email) {
        log.info("Sending email verification code to {}", email);

        String authCode = createAuthCode();
        String title = "[NCBT] 인증 번호";

        userMapper.deleteEmailVerification(email);

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .authCode(authCode)
                .expirationTime(LocalDateTime.now().plus(authCodeExpirationMillis, ChronoUnit.MILLIS))
                .build();
        userMapper.insertEmailVerification(emailVerification);

        mailService.sendEmail(email, title, "인증번호: " + authCode);
    }

    public void sendComplaintsToAdmin(PracticeComplaintsDTO practiceComplaintsDTO) {

        String title = "[NCBT] 문제 오류가 접수되었습니다.";

        String content =
                "제목 : " + practiceComplaintsDTO.getTitle()
                + "내용 : " + practiceComplaintsDTO.getContent();

        List<User> adminList = userMapper.findAdminUsers();

        if(adminList.size() > 0) {
            for (User user : adminList) {
                String email = user.getEmail();
                mailService.sendEmail(email, title, content);
            }
        }
    }

    public void sendSolvedMsgToUser(Long userId) {

        try {
            String email = userMapper.findEmailByUserId(userId);
            String title = "[NCBT] 관리팀에서 연락드립니다.";
            String content = "접수해주신 문제 오류가 해결되어 안내드립니다. 고객님의 관심에 감사드리며, 더욱더 발전하는 NCBT 서비스가 되겠습니다.";

            mailService.sendEmail(email, title, content);

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

}
