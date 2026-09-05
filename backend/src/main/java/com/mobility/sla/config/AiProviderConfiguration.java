package com.mobility.sla.config;

import com.mobility.sla.agent.LlmProvider;
import com.mobility.sla.agent.OpenAiLlmProvider;
import com.mobility.sla.agent.SarvamLlmProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiProviderConfiguration {

    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "sarvam")
    public LlmProvider sarvamLlmProvider(AiProperties aiProperties) {
        return new SarvamLlmProvider(aiProperties.getSarvam());
    }

    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
    public LlmProvider openAiLlmProvider(AiProperties aiProperties) {
        return new OpenAiLlmProvider(aiProperties.getOpenai());
    }
}
