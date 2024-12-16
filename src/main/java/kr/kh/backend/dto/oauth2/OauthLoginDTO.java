package kr.kh.backend.dto.oauth2;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OauthLoginDTO {

    private String code;
    private String state;

}
