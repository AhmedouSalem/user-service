package com.aryan.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
        "spring.profiles.active=test"
})
@ComponentScan(basePackages = "com.aryan.userservice")
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
