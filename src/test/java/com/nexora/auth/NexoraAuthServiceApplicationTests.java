package com.nexora.auth;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Disabled("Disabled until test configuration is prepared")
@SpringBootTest
@Import(TestPostgresContainer.class)
class NexoraAuthServiceApplicationTests {

}
