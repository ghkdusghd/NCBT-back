package kr.kh.backend.mapper;

import kr.kh.backend.dto.BookmarkDTO;
import kr.kh.backend.dto.PracticeComplaintsDTO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PracticeMapper {

    // 북마크 추가
    @Insert("INSERT INTO bookmarks (subject_id, question_id, user_id) VALUES (#{subjectId}, #{questionId}, #{userId})")
    void addBookmark(BookmarkDTO bookmarkDTO);

    // 북마크 여부 확인
    @Select("SELECT * FROM bookmarks WHERE user_id = #{userId} AND question_id = #{questionId}")
    BookmarkDTO findBookmarkByUserIdAndQuestionId(Long userId, Long questionId);

    // 북마크 삭제
    @Delete("DELETE FROM bookmarks WHERE id = #{bookmarkId}")
    void deleteBookmark(Long bookmarkId);

    // 문제 오류 신고시 추가
    @Insert("INSERT INTO question_complaints (subject_id, title, comment, user_id, subject_question_id) " +
            "VALUES (#{subjectId}, #{title}, #{content}, #{userId}, #{subjectQuestionId})")
    int addComplaint(PracticeComplaintsDTO practiceComplaintsDTO);


    // 중복 신고 여부 확인
    @Select("SELECT * FROM question_complaints WHERE user_id = #{userId} AND subject_question_id = #{subjectQuestionId}")
    PracticeComplaintsDTO findComplaintByUserIdAndSubjectQuestionId(Long userId, Long subjectQuestionId);
}

