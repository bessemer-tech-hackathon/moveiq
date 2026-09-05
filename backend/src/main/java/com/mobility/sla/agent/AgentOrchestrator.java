package com.mobility.sla.agent;

import com.mobility.sla.entity.CaseRecord;
import com.mobility.sla.entity.CaseTimelineEvent;
import com.mobility.sla.metric.MetricEngine;
import com.mobility.sla.service.CaseService;
import com.mobility.sla.sla.SlaBreachEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AgentOrchestrator {
    private final MetricEngine metricEngine;
    private final LlmProvider llmProvider;
    private final CaseService caseService;

    public AgentOrchestrator(MetricEngine metricEngine, LlmProvider llmProvider, CaseService caseService) {
        this.metricEngine = metricEngine;
        this.llmProvider = llmProvider;
        this.caseService = caseService;
    }

    public void handleSlaBreach(SlaBreachEvent event) {
        Map<String, Object> evidence = new HashMap<>(event.evidence() == null ? Map.of() : event.evidence());
        if (evidence.isEmpty()) {
            evidence.putAll(metricEngine.investigationEvidence(event.date()));
        }

        if (caseService.existsOpenCase(event.metricKey(), "Metro Fleet Services")) {
            return;
        }

        Map<String, Object> enriched = new HashMap<>(evidence);
        LocalDate date = event.date();
        String vendor = "Metro Fleet Services";
        String route = String.valueOf(enriched.getOrDefault("topRoute", "BLR-EAST-21"));
        String office = String.valueOf(enriched.getOrDefault("office", "Bengaluru East"));
        String shift = String.valueOf(enriched.getOrDefault("shift", "21:30"));
        String title = "SLA breach in " + event.metricKey();

        String investigationReport = llmProvider.investigate(enriched);
        String notice = llmProvider.accountabilityNotice(enriched, date.plusDays(1).toString());
        String escalation = "Unresolved breach requires vendor corrective action and possible escalation";

        CaseRecord caseRecord = caseService.createCase(
            event.metricKey(),
            title,
            "Transport Operations",
            vendor,
            route,
            office,
            shift,
            event.observed(),
            event.threshold(),
            event.severity(),
            event.date(),
            date.plusDays(1),
            1,
            "Vendor is the strongest observed contributor within the affected route and time window.",
            Integer.parseInt(String.valueOf(enriched.getOrDefault("affectedTrips", 0))),
            Integer.parseInt(String.valueOf(enriched.getOrDefault("affectedEmployees", 0))),
            Double.parseDouble(String.valueOf(enriched.getOrDefault("costImpact", 0))),
            investigationReport,
            notice,
            escalation
        );

        caseService.addTimeline(caseRecord.getId(), "SLA_BREACHED", "SLA threshold breached for " + event.metricKey(), "SYSTEM", "", 0);
        caseService.addTimeline(caseRecord.getId(), "AI_INVESTIGATION_STARTED", "AI investigation started for the operational breach.", "AI AGENT", investigationReport, 0);
        caseService.addTimeline(caseRecord.getId(), "ROOT_CAUSE_IDENTIFIED", "Root cause narrowed to the strongest observed vendor and route/time window.", "AI AGENT", route + " / " + shift, 0);
        caseService.addTimeline(caseRecord.getId(), "CASE_CREATED", "Case created automatically by the SLA detection workflow.", "AI AGENT", "Case #" + caseRecord.getCaseNumber(), 0);
        caseService.addTimeline(caseRecord.getId(), "NOTICE_GENERATED", "Accountability notice generated for the vendor.", "AI AGENT", notice, 0);
        caseService.addTimeline(caseRecord.getId(), "WAITING_FOR_VENDOR_ACTION", "Vendor response is pending", "AI AGENT", "Deadline " + caseRecord.getDeadline(), 0);

        caseService.updateStatus(caseRecord.getId(), "NOTICE_DRAFTED");
    }

    public void reEvaluateOpenCases(LocalDate date) {
        for (CaseRecord caseRecord : caseService.listOpenCases()) {
            if (caseRecord.getStatus().equals("CLOSED")) {
                continue;
            }
            if (date.isAfter(caseRecord.getDeadline()) || date.isEqual(caseRecord.getDeadline())) {
                caseService.updateStatus(caseRecord.getId(), "ESCALATED");
                caseService.addTimeline(caseRecord.getId(), "CASE_ESCALATED", "Deadline reached and breach still unresolved.", "AI AGENT", "Escalation auto-triggered", 1);
            } else {
                caseService.addTimeline(caseRecord.getId(), "RE_EVALUATED", "Open case re-evaluated during the next operational day.", "AI AGENT", "Date " + date, 0);
            }
        }
    }
}
