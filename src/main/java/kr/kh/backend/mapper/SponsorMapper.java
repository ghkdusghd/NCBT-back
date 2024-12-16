package kr.kh.backend.mapper;

import kr.kh.backend.domain.Payments;
import kr.kh.backend.domain.Sponsor;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SponsorMapper {

    // 고객키 생성
    @Insert("INSERT INTO customer_keys (customer_key) VALUES (#{customerKey})")
    void insertCustomerKey(@Param("customerKey") String customerKey);

    // 주문 삽입
    @Insert("INSERT INTO sponsor (order_id, order_name, customer_name, customer_email, amount, status, created_at) " +
            "VALUES (#{orderId}, #{orderName}, #{customerName}, #{customerEmail}, #{amount}, #{status}, #{createdAt})")
    void insertSponsor(Sponsor sponsor);

    // 결제 정보 삽입
    @Insert("INSERT INTO payments (order_id, payment_key, amount, pay_type, status, paid_at) " +
            "VALUES (#{orderId}, #{paymentKey}, #{amount}, #{payType}, #{status}, #{paidAt})")
    void insertPayment(Payments payment);

    // ID로 주문 조회
    @Select("SELECT * FROM sponsor WHERE id = #{id}")
    Sponsor selectSponsorById(@Param("id") Long id);

    // Order ID로 주문 조회
    @Select("SELECT * FROM sponsor WHERE order_id = #{orderId}")
    Sponsor selectSponsorBySponsorId(@Param("orderId") String orderId);

    // 모든 주문 조회
    @Select("SELECT * FROM sponsor")
    List<Sponsor> selectAllSponsors();

    // 주문 상태 업데이트
    @Update("UPDATE sponsor SET status = #{status} WHERE sponsor_id = #{sponsorId}")
    void updateSponsorStatus(@Param("orderId") String orderId, @Param("status") String status);
}

