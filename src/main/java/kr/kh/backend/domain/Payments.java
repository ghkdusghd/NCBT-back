package kr.kh.backend.domain;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Payments {
    private Long id;
    private String orderId;
    private String paymentKey;
    private Integer amount;
    private String payType;
    private String status;
    private Timestamp paidAt;
}
