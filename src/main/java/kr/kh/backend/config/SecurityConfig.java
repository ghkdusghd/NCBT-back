package kr.kh.backend.config;

import jakarta.servlet.http.HttpSession;
import kr.kh.backend.handler.Oauth2LoginFailureHandler;
import kr.kh.backend.handler.Oauth2LoginSuccessHandler;
import kr.kh.backend.security.jwt.JwtAuthFilter;
import kr.kh.backend.security.jwt.JwtTokenProvider;
import kr.kh.backend.service.security.CustomUserDetailsService;
import kr.kh.backend.service.security.Oauth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Oauth2UserService oauth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HttpSession httpSession) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/form/**").permitAll() // 일반로그인, 회원가입 요청 허용
                                .requestMatchers("/login/**").permitAll() // 소셜로그인, 회원가입 요청 허용
                                .requestMatchers("/ranking/**").permitAll() // 메인 페이지 요청 허용
                                .requestMatchers("/sponsor/**").permitAll() // 후원 관련
                                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // ADMIN 권한이 있어야 요청할 수 있는 경로
                                .anyRequest().authenticated() // 그 밖의 요청은 인증 필요
                )

                /**
                 * UsernamePasswordAuthenticationFilter 전에 JwtAuthFilter 를 거치도록 한다.
                 * 토큰이 유효한지 검사하고 유효하다면 인증 객체 (Authentication) 를 가져와서 Security Context 에 저장한다.
                 * (요청을 처리하는 동안 인증 정보를 유지하기 위함)
                 */
                .addFilterBefore(new JwtAuthFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

//                .formLogin(
//                        httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
//                                .loginPage("/form/login")
//                                .successHandler(LoginSuccessHandler())
//                                .failureHandler(LoginFailHandler())
//                        );
//
                /**
                 * 스프링 시큐리티에서는 oauth2 제공자 (네이버, 깃허브) 로부터 사용자의 로그인 정보를 자동으로 받아온다.
                 * 받아온 사용자 정보는 oauth2UserService 에서 작업한다.
                 */
                .oauth2Login(
                        httpSecurityOAuth2LoginConfigurer -> httpSecurityOAuth2LoginConfigurer
                                .successHandler(oauth2LoginSuccessHandler())
                                .failureHandler(oauth2LoginFailureHandler())
                                .userInfoEndpoint(
                                        userInfoEndpointConfig -> userInfoEndpointConfig
                                                .userService(oauth2UserService)
                                )
                );

        return http.build();
    }

    // security에 cors 합침
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Set-Cookie");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public Oauth2LoginSuccessHandler oauth2LoginSuccessHandler() {
        return new Oauth2LoginSuccessHandler(jwtTokenProvider);
    }

    @Bean
    public Oauth2LoginFailureHandler oauth2LoginFailureHandler() {
        return new Oauth2LoginFailureHandler();
    }

    // 커스텀한 UserDetailsService 를 스프링에서 인식하도록 AuthenticationManager 에 등록한다.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

}