package com.mobility.sla.sla;

import com.mobility.sla.agent.AgentOrchestrator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SlaBreachConsumer {
    private final AgentOrchestrator agentOrchestrator;

    public SlaBreachConsumer(AgentOrchestrator agentOrchestrator) {
        this.agentOrchestrator = agentOrchestrator;
    }

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consume(SlaBreachMessage message) {
        agentOrchestrator.handleSlaBreach(new SlaBreachEvent(message.metricKey(), message.observed(),
            message.threshold(), message.severity(), message.date(), message.evidence()));
    }
}