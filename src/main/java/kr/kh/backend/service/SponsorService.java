package kr.kh.backend.service;

import kr.kh.backend.config.TossConfig;
import kr.kh.backend.domain.Payments;
import kr.kh.backend.domain.Sponsor;
import kr.kh.backend.mapper.SponsorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Base64;

@Service
public class SponsorService {
    private final SponsorMapper sponsorMapper;
    private final TossConfig tossConfig;

    @Autowired
    public SponsorService(SponsorMapper sponsorMapper, TossConfig tossConfig) {
        this.sponsorMapper = sponsorMapper;
        this.tossConfig = tossConfig;
    }

    // 고객키 생성
    public String generateCustomerKey() {
        // 고객 키 생성 - UUID 아니면 형식 에러로 결제 UI 생성이 안됨
        String customerKey = UUID.randomUUID().toString();

        // 데이터베이스에 저장
        sponsorMapper.insertCustomerKey(customerKey);

        return customerKey;
    }

    // 주문 생성
    public void createOrder(String orderId, String orderName, String customerName, String customerEmail, Integer amount) {
        Sponsor sponsor = new Sponsor();
        sponsor.setOrderId(orderId);
        sponsor.setOrderName(orderName);
        sponsor.setCustomerName(customerName);
        sponsor.setCustomerEmail(customerEmail);
        sponsor.setAmount(amount);
        sponsor.setStatus("PENDING");
        sponsor.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        sponsorMapper.insertSponsor(sponsor);
    }

    // 결제 인증 - 쿼리 파라미터로 받은거 저장 - 테이블을 하나로 합치는게 더 좋을까..
    public void verifyPayment(String paymentType, String orderId, String paymentKey, String amount) throws Exception {
        // Toss Payments API 호출
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String tossSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6"; // 테스트용 시크릿키
        String encodedSecretKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

        System.out.println("Toss Secret Key: " + tossSecretKey); // 잘 출력됨
        System.out.println("Toss Encrypted Secret Key: " + encodedSecretKey);

        headers.set("Authorization", "Basic " + encodedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("orderId", orderId);
        requestBody.put("paymentKey", paymentKey);
        requestBody.put("amount", amount);

        System.out.println("###########Request Body: " + requestBody); // 요청 데이터 출력
        System.out.println("############Headers: " + headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    new HttpEntity<>(requestBody, headers),
                    String.class
            );

            // 응답 상태 코드 및 본문 출력
            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if (response.getStatusCode() != HttpStatus.OK) {
                System.out.println("Response Status Code 제발 문제가 뭔지 알려줘: " + response.getStatusCode());
                System.out.println("Response Body 제발 문제가 뭔지 알려줘: " + response.getBody());
                throw new Exception("결제 검증 실패");
            }

        } catch (Exception e) {
            System.out.println("API 요청 중 에러 발생: " + e.getMessage());
            e.printStackTrace();  // 이미 처리된 결제입니다라고!?!?
        }

        // 결제 데이터 저장
        Sponsor sponsor = sponsorMapper.selectSponsorBySponsorId(orderId);
        if (sponsor == null) {
            throw new Exception("유효하지 않은 주문 ID");
        }

        // 결제 승인시 결제 정보 저장
        try {
            int amountInt = Integer.parseInt(amount);

            Payments payment = new Payments();
            payment.setOrderId(orderId);
            payment.setPaymentKey(paymentKey);
            payment.setAmount(amountInt);
            payment.setPayType(paymentType);
            payment.setStatus("COMPLETED");
            payment.setPaidAt(new Timestamp(System.currentTimeMillis()));

            sponsorMapper.insertPayment(payment);

            // Sponsor 상태 업데이트
            sponsorMapper.updateSponsorStatus(orderId, "COMPLETED");
        } catch (NumberFormatException e) {
            throw new Exception("amount 값을 숫자로 변환할 수 없습니다: " + amount);
        }
    }


    public Sponsor getOrderById(Long id) {
        return sponsorMapper.selectSponsorById(id);
    }

    public Sponsor getOrderByOrderId(String orderId) {
        return sponsorMapper.selectSponsorBySponsorId(orderId);
    }

    public List<Sponsor> getAllOrders() {
        return sponsorMapper.selectAllSponsors();
    }

    public void updateOrderStatus(String orderId, String status) {
        sponsorMapper.updateSponsorStatus(orderId, status);
    }
}
