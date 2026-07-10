package com.esprit.microservice.tracking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TrackingApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context loads without errors
    }
}
