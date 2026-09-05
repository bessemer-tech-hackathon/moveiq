package com.mobility.sla.agent;

import java.util.Map;

public interface LlmProvider {
    String investigate(Map<String, Object> evidence);
    String accountabilityNotice(Map<String, Object> evidence, String deadline);
    String leadershipSummary(Map<String, Object> evidence, int openCases, int escalatedCases);
}