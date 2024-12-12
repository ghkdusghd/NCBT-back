package kr.kh.backend.dto.oauth2;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class GithubUserDTO {

    private Long id;
    private String login; // GitHub username
    private String name;
    private String email;
    private String avatar_url;

}
