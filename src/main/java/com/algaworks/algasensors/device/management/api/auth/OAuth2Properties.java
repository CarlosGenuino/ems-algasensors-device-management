package com.algaworks.algasensors.device.management.api.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "oauth2.client")
public class OAuth2Properties {
    
    private String tokenUri;
    private String clientId;
    private String clientSecret;
    private String grantType = "client_credentials";
}
