package kr.kh.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String title, String content) {
//        SimpleMailMessage emailForm = new SimpleMailMessage();
        SimpleMailMessage emailForm = createEmail(toEmail, title, content); // 메시지 생성


        try{
            mailSender.send(emailForm);
            log.info("Email sent to {}", toEmail);
        }catch(Exception e){
            log.debug(e.getMessage());
            log.error("Failed to send email: {}", e.getMessage(), e);
        }
    }

    // 발송할 이메일 정보
    public SimpleMailMessage createEmail(String toEmail, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(content);

        return message;
    }
}

