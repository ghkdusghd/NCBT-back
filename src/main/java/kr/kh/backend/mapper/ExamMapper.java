package kr.kh.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExamMapper {

    @Insert("INSERT INTO first_score(score, user_id, subject_id, created_at) " +
            "SELECT #{score}, #{userId}, #{subjectId}, sysdate() " +
            "WHERE NOT EXISTS ( " +
            "SELECT 1 " +
            "FROM first_score " +
            "WHERE user_id = #{userId} " +
            ") ")
    public int recordFirstScore(@Param("userId") Long userId,
                                @Param("score") int score,
                                @Param("subjectId") int subjectId);

    @Insert("INSERT INTO last_score(score, user_id, subject_id, updated_at) " +
            "VALUES (#{score}, #{userId}, #{subjectId}, sysdate()) ")
    public int recordLastScore(@Param("userId") Long userId,
                               @Param("score") int score,
                               @Param("subjectId") int subjectId);

}
