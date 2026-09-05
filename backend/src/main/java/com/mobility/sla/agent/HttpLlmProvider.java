package com.mobility.sla.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobility.sla.config.AiProperties;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

abstract class HttpLlmProvider implements LlmProvider {
    private final AiProperties.Provider config;
    private final String authorizationHeader;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    protected HttpLlmProvider(AiProperties.Provider config, String authorizationHeader) {
        this.config = config;
        this.authorizationHeader = authorizationHeader;
        if (config.getApiKey() == null || config.getApiKey().isBlank()) {
            throw new IllegalStateException("AI provider API key is required for the configured provider");
        }
        if (config.getEndpoint() == null || config.getEndpoint().isBlank()) {
            throw new IllegalStateException("AI provider endpoint is required for the configured provider");
        }
    }

    protected String complete(String instruction, Map<String, Object> evidence) {
        try {
            String prompt = instruction + "\nEvidence JSON:\n" + objectMapper.writeValueAsString(evidence);
            String body = objectMapper.writeValueAsString(Map.of(
                "model", config.getModel(),
                "messages", new Object[] { Map.of("role", "user", "content", prompt) }
            ));
            HttpRequest request = HttpRequest.newBuilder(URI.create(config.getEndpoint()))
                .header("Authorization", authorizationHeader + config.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new IllegalStateException("AI provider request failed with HTTP " + response.statusCode() + ": " + response.body());
            }
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode content = root.at("/choices/0/message/content");
            if (content.isMissingNode() || content.asText().isBlank()) {
                throw new IllegalStateException("AI provider response did not contain message content");
            }
            return content.asText();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("AI provider request was interrupted", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("AI provider request failed", exception);
        }
    }
}