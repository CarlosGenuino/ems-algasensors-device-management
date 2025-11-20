package com.algaworks.algasensors.device.management.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class AuthRestClientConfig {
    
    private final OAuth2Properties oauth2Properties;
    
    @Bean
    public RestClient authRestClient(RestClient.Builder builder) {
        return builder
            .baseUrl(getBaseUrl())
            .requestFactory(generateClientHttpRequestFactory())
            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                throw new RuntimeException("OAuth2 authentication failed with status: " + response.getStatusCode());
            })
            .build();
    }
    
    private String getBaseUrl() {
        // Extrai a base URL do token URI
        String tokenUri = oauth2Properties.getTokenUri();
        int pathIndex = tokenUri.indexOf("/auth");
        if (pathIndex > 0) {
            return tokenUri.substring(0, pathIndex);
        }
        return "http://authurl.com";
    }
    
    private ClientHttpRequestFactory generateClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        return requestFactory;
    }
}
