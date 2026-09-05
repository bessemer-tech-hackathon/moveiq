package com.mobility.sla.service;

import com.mobility.sla.entity.SimulationState;
import com.mobility.sla.metric.MetricEngine;
import com.mobility.sla.repository.SimulationStateRepository;
import com.mobility.sla.sla.SlaBreachEvent;
import com.mobility.sla.sla.SlaEvaluationService;
import com.mobility.sla.agent.AgentOrchestrator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimulationService {
    private final SimulationStateRepository simulationStateRepository;
    private final MetricEngine metricEngine;
    private final SlaEvaluationService slaEvaluationService;
    private final CaseService caseService;
    private final AgentOrchestrator agentOrchestrator;

    public SimulationService(SimulationStateRepository simulationStateRepository, MetricEngine metricEngine,
                            SlaEvaluationService slaEvaluationService, CaseService caseService, AgentOrchestrator agentOrchestrator) {
        this.simulationStateRepository = simulationStateRepository;
        this.metricEngine = metricEngine;
        this.slaEvaluationService = slaEvaluationService;
        this.caseService = caseService;
        this.agentOrchestrator = agentOrchestrator;
    }

    public LocalDate getCurrentDate() {
        return simulationStateRepository.findById(1L)
            .map(SimulationState::getOperationalDate)
            .orElse(LocalDate.of(2026, 7, 15));
    }

    public Map<String, Object> advanceDay() {
        LocalDate previous = getCurrentDate();
        LocalDate next = previous.plusDays(1);
        SimulationState state = simulationStateRepository.findById(1L).orElse(new SimulationState());
        state.setId(1L);
        state.setOperationalDate(next);
        simulationStateRepository.save(state);

        agentOrchestrator.reEvaluateOpenCases(next);
        List<SlaBreachEvent> breaches = slaEvaluationService.evaluate(next);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("previousDate", previous.toString());
        result.put("newDate", next.toString());
        result.put("metricsEvaluated", metricEngine.dashboard(next));
        result.put("breachesDetected", breaches.size());
        result.put("breachesQueued", breaches.size());
        result.put("casesReevaluated", caseService.listOpenCases().size());
        result.put("casesEscalated", 0);
        return result;
    }
}
