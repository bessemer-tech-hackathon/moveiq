package com.mobility.sla.controller;

import com.mobility.sla.dto.MetricResponse;
import com.mobility.sla.entity.CaseRecord;
import com.mobility.sla.entity.CaseTimelineEvent;
import com.mobility.sla.metric.MetricEngine;
import com.mobility.sla.service.CaseService;
import com.mobility.sla.service.SimulationService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final MetricEngine metricEngine;
    private final CaseService caseService;
    private final SimulationService simulationService;

    public ApiController(MetricEngine metricEngine, CaseService caseService, SimulationService simulationService) {
        this.metricEngine = metricEngine;
        this.caseService = caseService;
        this.simulationService = simulationService;
    }

    @GetMapping("/metrics")
    public List<Map<String, Object>> metrics() {
        return metricEngine.dashboard(LocalDate.of(2026, 7, 15));
    }

    @GetMapping("/metrics/{metric}")
    public MetricResponse metric(@PathVariable String metric,
                                @RequestParam(required = false) String date,
                                @RequestParam(required = false) String vendor,
                                @RequestParam(required = false) String office,
                                @RequestParam(required = false) String shift) {
        LocalDate resolvedDate = date == null ? LocalDate.of(2026, 7, 15) : LocalDate.parse(date);
        Map<String, Object> data = metricEngine.getMetric(metric, resolvedDate, Map.of("vendor", vendor == null ? "" : vendor, "office", office == null ? "" : office, "shift", shift == null ? "" : shift));
        return new MetricResponse(
            String.valueOf(data.getOrDefault("metricKey", metric)),
            String.valueOf(data.getOrDefault("title", metric)),
            String.valueOf(data.getOrDefault("description", "")),
            ((Number) data.getOrDefault("value", 0)).doubleValue(),
            String.valueOf(data.getOrDefault("unit", "")),
            ((Number) data.getOrDefault("sla", 0)).doubleValue(),
            ((Number) data.getOrDefault("baseline", 0)).doubleValue(),
            ((Number) data.getOrDefault("trend", 0)).doubleValue(),
            String.valueOf(data.getOrDefault("status", "HEALTHY")),
            resolvedDate.toString(),
            new HashMap<>()
        );
    }

    @GetMapping("/cases")
    public List<CaseRecord> cases() {
        return caseService.listAll();
    }

    @GetMapping("/cases/{id}")
    public CaseRecord caseById(@PathVariable Long id) {
        return caseService.getById(id).orElseThrow();
    }

    @GetMapping("/cases/{id}/timeline")
    public List<CaseTimelineEvent> timeline(@PathVariable Long id) {
        return caseService.timeline(id);
    }

    @GetMapping("/alerts")
    public List<CaseRecord> alerts() { return caseService.listAll().stream().filter(c -> !"CLOSED".equals(c.getStatus())).toList(); }

    @GetMapping("/reports")
    public List<CaseRecord> reports() { return caseService.listAll().stream().filter(c -> c.getInvestigationReport() != null && !c.getInvestigationReport().isBlank()).toList(); }

    @GetMapping("/vendors")
    public Object vendors() { return metricEngine.investigationEvidence(simulationService.getCurrentDate()).get("vendorGranularity"); }

    @GetMapping("/employees")
    public Object employees() { return metricEngine.investigationEvidence(simulationService.getCurrentDate()).get("employeeGranularity"); }

    @GetMapping("/simulation/status")
    public Map<String, Object> simulationStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("currentDate", simulationService.getCurrentDate().toString());
        result.put("metrics", metricEngine.dashboard(simulationService.getCurrentDate()));
        return result;
    }

    @PostMapping("/simulation/advance-day")
    public Map<String, Object> advanceDay() {
        return simulationService.advanceDay();
    }
}
