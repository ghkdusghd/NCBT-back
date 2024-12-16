package kr.kh.backend.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubjectQuestion {

    private int subjectQuestionId;
    private int subjectId;
    private int userId;
    private int questionId;

}
