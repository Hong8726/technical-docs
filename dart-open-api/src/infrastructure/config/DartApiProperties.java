package com.hong.diclosure.dart.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@ConfigurationProperties(prefix = "dart.api")
public class DartApiProperties {

    private String baseUrl;
    private String apiKey;
    private int timeout;
    private int maxRetries;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}

