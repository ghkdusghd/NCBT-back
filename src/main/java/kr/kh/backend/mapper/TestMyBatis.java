package kr.kh.backend.mapper;

import kr.kh.backend.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TestMyBatis {
    @Select("SELECT * FROM user")
    List<User> selectAll();
}
