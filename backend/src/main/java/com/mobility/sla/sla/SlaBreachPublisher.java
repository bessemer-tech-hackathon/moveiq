package com.mobility.sla.sla;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlaBreachPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public SlaBreachPublisher(RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange}") String exchange,
            @Value("${rabbitmq.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(SlaBreachMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}