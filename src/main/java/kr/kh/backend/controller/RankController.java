package kr.kh.backend.controller;

import kr.kh.backend.dto.RankDTO;
import kr.kh.backend.exception.CustomException;
import kr.kh.backend.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RankController {

    private final RankService rankService;

    @PostMapping("/ranking/v1")
    public ResponseEntity<List<RankDTO>> getRankingV1(@RequestBody RankDTO rankDTO) {
        if(rankDTO == null || rankDTO.getTitle() == null || rankDTO.getTable() == null) {
            throw new CustomException(
                    "title 및 table 정보는 필수입니다.",
                    "INVALID_RANK_INFO",
                    HttpStatus.BAD_REQUEST
            );
        }
        return ResponseEntity.ok(rankService.getRankingV1(rankDTO));
    }

    @PostMapping("/ranking/v2")
    public ResponseEntity<Map<String, List>> getRankingV2(@RequestBody RankDTO rankDTO) {
        log.info("랭킹 컨트롤러 !!!!!!");
        if(rankDTO == null || rankDTO.getTitle() == null) {
            throw new CustomException(
                    "title 정보는 필수입니다.",
                    "INVALID_RANK_INFO",
                    HttpStatus.BAD_REQUEST
            );
        }
        return ResponseEntity.ok(rankService.getRankingV2(rankDTO));
    }
}
