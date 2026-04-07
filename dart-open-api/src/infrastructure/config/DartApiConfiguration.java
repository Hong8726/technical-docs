package com.hong.diclosure.dart.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author 홍보람 (qhfka2854@gmail.com)
 */
@Configuration
@EnableConfigurationProperties(DartApiProperties.class)
public class DartApiConfiguration {

    @Bean
    RestTemplate dartRestTemplate(RestTemplateBuilder builder, DartApiProperties properties) {
        int timeout = Math.max(properties.getTimeout(), 1_000);
        return builder
                .rootUri(properties.getBaseUrl())
                .requestFactory(() -> createRequestFactory(timeout))
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory(int timeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return factory;
    }
}

