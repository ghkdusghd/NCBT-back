package kr.kh.backend.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Sponsor {
    private Long id;
    private String orderId;
    private String orderName;
    private String customerName;
    private String customerEmail;
    private Integer amount;
    private String status;
    private Timestamp createdAt;
}
