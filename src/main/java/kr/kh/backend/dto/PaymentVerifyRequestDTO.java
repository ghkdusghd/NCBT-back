package kr.kh.backend.dto;

import lombok.Data;

@Data
public class PaymentVerifyRequestDTO {
    private String paymentType;
    private String orderId;
    private String paymentKey;
    private String amount;
}
