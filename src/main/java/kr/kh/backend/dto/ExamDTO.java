package kr.kh.backend.dto;

import lombok.Data;

@Data
public class ExamDTO {
    private int ScoreId;
    private int score;
    private int userId;
    private int subjectId;
}
