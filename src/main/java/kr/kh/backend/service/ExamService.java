package kr.kh.backend.service;

import kr.kh.backend.dto.ExamDTO;
import kr.kh.backend.mapper.ExamMapper;
import kr.kh.backend.mapper.UserMapper;
import kr.kh.backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamService {

    private final JwtTokenProvider jwtTokenProvider;
    private final ExamMapper examMapper;
    private final UserMapper userMapper;

    public ResponseEntity<?> recordScore(ExamDTO examDTO, String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);


        if(token == null) {
            return ResponseEntity.badRequest().body("토큰이 만료되었거나 인증할 수 없는 사용자 정보입니다.");
        }

        // username 으로 id 찾기
        Long userId = userMapper.findUserIdByUsername(username);

        if(userId == null) {
            return ResponseEntity.badRequest().body("등록되지 않은 사용자입니다.");
        }

        // 해당 id 로 처음 등록한 점수가 있는지 조회하고, 없으면 insert 한다.
        int firstResult = examMapper.recordFirstScore(userId, examDTO.getScore(), examDTO.getSubjectId());
        int lastResult = 0;

        // 쿼리 실행 결과가 0이면 first_score 테이블에 이미 점수가 있는 것이므로 last_score 에 insert 한다.
        if(firstResult == 0) {
            lastResult = examMapper.recordLastScore(userId, examDTO.getScore(), examDTO.getSubjectId());
        }

        if(lastResult == 0) {
            return ResponseEntity.badRequest().body("점수 등록에 실패했습니다.");
        }

        return ResponseEntity.ok("점수 등록이 완료되었습니다.");
    }

}
