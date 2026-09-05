package com.mobility.sla.agent;

import com.mobility.sla.config.AiProperties;
import java.util.Map;

public class OpenAiLlmProvider extends HttpLlmProvider {

    public OpenAiLlmProvider(AiProperties.Provider config) {
        super(config, "Bearer ");
    }

    @Override
    public String investigate(Map<String, Object> evidence) {
        return complete("Investigate this transport SLA breach. Return evidence, baseline comparison, affected scope, root cause, business impact, recommended action, and escalation decision.", evidence);
    }

    @Override
    public String accountabilityNotice(Map<String, Object> evidence, String deadline) {
        return complete("Draft a factual vendor accountability notice for the breach evidence. The response deadline is " + deadline + ".", evidence);
    }

    @Override
    public String leadershipSummary(Map<String, Object> evidence, int openCases, int escalatedCases) {
        return complete("Write a leadership summary for this evidence. Open cases: " + openCases + ". Escalated cases: " + escalatedCases + ".", evidence);
    }
}
