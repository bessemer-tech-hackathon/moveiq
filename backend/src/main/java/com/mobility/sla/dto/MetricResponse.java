package com.mobility.sla.dto;

import java.util.Map;

public record MetricResponse(
    String metricKey,
    String title,
    String description,
    double value,
    String unit,
    double sla,
    double baseline,
    double trend,
    String status,
    String period,
    Map<String, String> filters
) {
}
