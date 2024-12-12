package kr.kh.backend.mapper;

import kr.kh.backend.domain.EmailVerification;
import kr.kh.backend.domain.User;
import kr.kh.backend.dto.security.LoginDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE nickname = #{username}")
    User findByUsername(String username);

    @Select("SELECT email FROM user WHERE nickname = #{username}")
    String findEmailByUsername(String email);

    // 일반 로그인 insert
    @Insert("INSERT INTO user(email, nickname, password, roles) " +
            "VALUES (#{email}, #{username}, #{password}, #{roles})")
    void insertUser(LoginDTO loginDTO);

    // 소셜 로그인 insert
    @Insert("INSERT INTO user(email, nickname, roles, platform) " +
            "VALUES (#{email}, #{nickname}, #{roles}, #{platform})")
    void insertOauthUser(User user);

    @Select("SELECT COUNT(*) > 0 FROM user WHERE nickname = #{username}")
    boolean isUsernameExisted(String username);

    // 트큰의 username으로 user_id 조회
    @Select("SELECT id FROM user WHERE nickname = #{username}")
    Long findUserIdByUsername(String username);

    // 우리 user_id 는 int 아니었나용...
    @Select("SELECT id FROM user WHERE nickname = #{username}")
    int findId(String nickname);

    // 이메일 중복 확인
    @Select("SELECT COUNT(*) > 0 FROM user WHERE email = #{email}")
    boolean isEmailExisted(String email);

    // 이메일 인증 코드 저장
    @Insert("INSERT INTO email_verification (email, auth_code, expiration_time) " +
            "VALUES (#{email}, #{authCode}, #{expirationTime})")
    void insertEmailVerification(EmailVerification emailVerification);

    // Email로 이메일 인증 조회
    @Select("SELECT * FROM email_verification WHERE email = #{email}")
    EmailVerification findByVerifiedEmail(String email);

    // 인증 코드와 이메일 확인
    @Select("SELECT COUNT(*) = 1 FROM email_verification " +
            "WHERE email = #{email} AND auth_code = #{authCode}")
    boolean verifyAuthCode(String email, String authCode);

    // 인증 코드 삭제
    @Delete("DELETE FROM email_verification WHERE email = #{email}")
    void deleteEmailVerification(String email);

    // 계정 찾기
    @Select("SELECT nickname FROM user WHERE email = #{email}")
    String findUsernameByEmail(String email);

    // 비밀번호 재설정
    @Update("UPDATE user SET password = #{password} WHERE nickname = #{username}")
    void updatePassword(@Param("username") String username, @Param("password") String password);

    // 사용자 비밀번호 찾기
    @Select("SELECT password FROM user WHERE nickname = #{username}")
    String findPasswordByUsername(String username);

    // 관리자 계정 찾기
    @Select("SELECT * FROM user WHERE roles = 'ADMIN'")
    List<User> findAdminUsers();

    // id 로 사용자 이메일 찾기
    @Select("SELECT email FROM user WHERE id = #{userId}")
    String findEmailByUserId(Long userId);

    // email 로 User 객체 찾기
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);
}
