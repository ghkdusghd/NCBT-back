package kr.kh.backend.dto;

import lombok.Data;

@Data
public class BookmarkDTO {

    private Long id;
    private int subjectId;
    private int questionId;
    private int userId;

}

/* Post - body
    {
    "subjectId": 1,
    "questionId": 8
    }
*/

/* GET - parameter
    /bookmarks?questionId=19
*/