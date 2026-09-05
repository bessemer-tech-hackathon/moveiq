package com.mobility.sla.sla;
import com.mobility.sla.entity.SlaConfiguration;
import com.mobility.sla.metric.MetricEngine;
import com.mobility.sla.repository.SlaConfigurationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class SlaEvaluationService {
    private final MetricEngine metrics; private final SlaConfigurationRepository slas; private final SlaBreachPublisher breachPublisher;
    public SlaEvaluationService(MetricEngine metrics, SlaConfigurationRepository slas, SlaBreachPublisher breachPublisher) { this.metrics = metrics; this.slas = slas; this.breachPublisher = breachPublisher; }
    public List<SlaBreachEvent> evaluate(LocalDate date) {
        List<SlaBreachEvent> breaches = new ArrayList<>();
        for (SlaConfiguration sla : slas.findAll()) {
            if (!sla.isActive()) continue;
            Map<String, Object> metric = metrics.getMetric(sla.getMetricKey(), date, Map.of());
            double observed = ((Number) metric.get("value")).doubleValue();
            boolean breached = "GREATER_THAN".equals(sla.getComparisonOperator()) ? observed > sla.getThreshold() : observed < sla.getThreshold();
            if (breached) { SlaBreachEvent event = new SlaBreachEvent(sla.getMetricKey(), observed, sla.getThreshold(), sla.getSeverity(), date, metrics.investigationEvidence(date)); breaches.add(event); breachPublisher.publish(new SlaBreachMessage(event.metricKey(), event.observed(), event.threshold(), event.severity(), event.date(), event.evidence())); }
        }
        return breaches;
    }
}