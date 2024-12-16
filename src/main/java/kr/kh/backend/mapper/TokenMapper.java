package kr.kh.backend.mapper;

import kr.kh.backend.domain.Token;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TokenMapper {

    @Insert("INSERT INTO token (token, user_id, expiration_date, status) " +
            "VALUES (#{token.token}, #{token.userId}, #{token.expirationDate}, #{token.status})")
    int saveToken(@Param("token") Token token);

    @Select("SELECT * FROM token WHERE token = #{token} ORDER BY id DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "token", column = "token"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "expirationDate", column = "expiration_date"),
            @Result(property = "status", column = "status")
    })
    List<Token> getTokenByToken(String token);

    @Update("UPDATE token SET status = #{token.status} WHERE token = #{token.token}")
    int updateToken(@Param("token") Token token);

}
