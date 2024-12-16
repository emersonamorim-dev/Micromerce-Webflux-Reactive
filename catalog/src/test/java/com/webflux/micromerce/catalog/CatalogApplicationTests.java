package com.webflux.micromerce.catalog;

import com.webflux.micromerce.catalog.config.BaseIntegrationTest;
import com.webflux.micromerce.catalog.config.TestPostgresDBConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestPostgresDBConfig.class)
@ActiveProfiles("test")
class CatalogApplicationTests extends BaseIntegrationTest {

    @Test
    void contextLoads() {
    }
}