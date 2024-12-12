package kr.kh.backend.dto.oauth2;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class GithubLoginDTO {

    private String access_token;

}
