package com.webflux.micromerce.cart.infrastructure.monitoring;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class KibanaMetricsService {
    @Getter
    private final KibanaMetricsData metricsData = new KibanaMetricsData();
}
