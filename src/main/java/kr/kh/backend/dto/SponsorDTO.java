package kr.kh.backend.dto;

import lombok.Data;

@Data
public class SponsorDTO {
    private String orderId;
    private String orderName;
    private String customerName;
    private String customerEmail;
    private Integer amount;
}
