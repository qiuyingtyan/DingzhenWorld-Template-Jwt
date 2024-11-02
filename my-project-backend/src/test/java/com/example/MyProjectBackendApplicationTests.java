package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class MyProjectBackendApplicationTests {

    // 测试上下文加载
    @Test
    void contextLoads() {
        // 打印加密后的密码
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }

}
