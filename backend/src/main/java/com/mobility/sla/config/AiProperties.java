package com.mobility.sla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public class AiProperties {
    private String provider;
    private Provider sarvam = new Provider();
    private Provider openai = new Provider();
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public Provider getSarvam() { return sarvam; }
    public void setSarvam(Provider sarvam) { this.sarvam = sarvam; }
    public Provider getOpenai() { return openai; }
    public void setOpenai(Provider openai) { this.openai = openai; }
    public static class Provider {
        private String apiKey;
        private String model;
        private String endpoint;
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    }
}