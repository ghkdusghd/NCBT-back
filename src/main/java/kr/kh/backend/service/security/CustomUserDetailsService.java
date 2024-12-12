package kr.kh.backend.service.security;

import kr.kh.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        kr.kh.backend.domain.User user = userMapper.findByUsername(username);
        log.info("user = {}, auth = {}", user, user.getAuthorities());

        if(user != null) {
            // 해당하는 User 데이터가 있다면 UserDetails 객체로 만든다.
            return createUserDetails(user);
        } else {
            throw new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다.");
        }
    }

    private UserDetails createUserDetails(kr.kh.backend.domain.User user) {
        log.info("createUserDetails = {}", user);
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

}
