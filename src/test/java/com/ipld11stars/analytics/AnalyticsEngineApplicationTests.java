package com.ipld11stars.analytics;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AnalyticsEngineApplicationTests {

    @Test
    void contextLoads() {
        // Verifies complete Spring Boot context initializes cleanly
    }
}
