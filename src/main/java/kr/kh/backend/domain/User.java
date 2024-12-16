package kr.kh.backend.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Data
@ToString(exclude = "password")
@AllArgsConstructor
public class User implements UserDetails, OAuth2User {

    private int id;
    private String email;
    private String nickname;
    private String password;
    private String platform;
    private String roles;
    private Map<String, Object> attributes;

    public User() {
    }

    // FormLogin용 생성자
    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
        this.attributes = null;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles != null && !roles.isEmpty() ?
                List.of(roles.split(",")).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()) :
                new ArrayList<>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return nickname;
    }
}
