package kr.kh.backend.controller;

import kr.kh.backend.domain.User;
import kr.kh.backend.mapper.TestMyBatis;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class TestController {

    private final TestMyBatis testMyBatis;

    @GetMapping("/test")
    public List<User> test() {
        System.out.println("skdfsfdsljfkdlsfjldsjfdsjklfdskfs");
        return testMyBatis.selectAll();
    }

}
