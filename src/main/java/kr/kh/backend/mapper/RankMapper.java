package kr.kh.backend.mapper;

import kr.kh.backend.dto.RankDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RankMapper {

    /**
     * 쿼리문 수행시간 비교
     * v1. 여러 개의 쿼리문을 수행한다 (69ms)
     */
    @Select("SELECT id FROM subject WHERE title = #{title}")
    int findIdByTitle(String title);

    @Select("SELECT u.nickname, t.score " +
            "from user u " +
            "inner join ${table} t ON u.id = t.user_id " +
            "where t.subject_id = #{subjectId} " +
            "ORDER BY t.score DESC, t.created_at DESC LIMIT 5")
    List<RankDTO> findTop5V1(RankDTO rankDTO);

    /**
     * 쿼리문 수행시간 비교
     * v2. 조인 쿼리 한 개를 수행한다 (22ms)
     */
    @Select("SELECT u.nickname, t.score " +
            "FROM user u " +
            "INNER JOIN ${table} t ON u.id = t.user_id " +
            "WHERE t.subject_id = " +
            "(SELECT id FROM subject WHERE title = #{title}) " +
            "ORDER BY t.score DESC, t.created_at DESC LIMIT 5")
    List<RankDTO> findTop5V2(RankDTO rankDTO);
}
