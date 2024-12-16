package kr.kh.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmailVerificationDTO {
    private String email;
    private String authCode;
    private LocalDateTime expirationTime;

    public EmailVerificationDTO(String email, String authCode, LocalDateTime expirationTime) {
        this.email = email;
        this.authCode = authCode;
        this.expirationTime = expirationTime;
    }

}
