package kr.kh.backend.mapper;

import kr.kh.backend.dto.PracticeComplaintsDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("SELECT * FROM question_complaints WHERE is_solved = 0")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "subjectQuestionId", column = "subject_question_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "comment"),
            @Result(property = "isSolved", column = "is_solved")
    })
    public List<PracticeComplaintsDTO> selectAllComplaints();

    @Update("UPDATE question_complaints SET is_solved = 1 WHERE id = #{id}")
    public int updateSolvedComplaints(@Param("id") int id);

}
