package com.mobility.sla.sla;
import java.time.LocalDate;
import java.util.Map;
public record SlaBreachEvent(String metricKey, double observed, double threshold, String severity, LocalDate date, Map<String, Object> evidence) { }