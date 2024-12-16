package com.webflux.micromerce.catalog.infrastructure.cache;

import com.webflux.micromerce.catalog.config.BaseIntegrationTest;
import com.webflux.micromerce.catalog.infrastructure.monitoring.KibanaMetricsSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class RedisMetricsServiceTest extends BaseIntegrationTest {

    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @MockBean
    private KibanaMetricsSender kibanaMetricsSender;

    private RedisMetricsService redisMetricsService;

    @BeforeEach
    void setUp() {
        redisMetricsService = new RedisMetricsService(Optional.of(redisTemplate), kibanaMetricsSender);
    }

    @Test
    void monitorRedisMetrics_Success() {
        // Arrange
        when(kibanaMetricsSender.sendMetricsToKibana(anyString(), anyString()))
            .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(redisMetricsService.monitorRedisMetrics())
                .verifyComplete();

        verify(kibanaMetricsSender).sendMetricsToKibana(anyString(), anyString());
    }

    @Test
    void recordMetric_Success() {
        // Arrange
        String key = "test-key";
        String value = "test-value";

        // Act & Assert
        StepVerifier.create(redisMetricsService.recordMetric(key, value))
                .verifyComplete();

        // Verify the value was stored
        StepVerifier.create(redisTemplate.opsForValue().get(key))
                .expectNext(value)
                .verifyComplete();
    }

    @Test
    void getMetric_Success() {
        // Arrange
        String key = "test-key";
        String value = "test-value";

        // First store a value
        StepVerifier.create(redisTemplate.opsForValue().set(key, value))
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        // Act & Assert
        StepVerifier.create(redisMetricsService.getMetric(key))
                .expectNext(value)
                .verifyComplete();
    }

    @Test
    void getMetric_NotFound() {
        // Act & Assert
        StepVerifier.create(redisMetricsService.getMetric("non-existent-key"))
                .verifyComplete();
    }
}
