package com.algaworks.algasensors.device.management.api.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final RestClient authRestClient;
    private final OAuth2Properties oauth2Properties;
    
    private String cachedToken;
    private Instant tokenExpirationTime;
    
    /**
     * Obtém um token de acesso válido. Utiliza cache se o token ainda for válido.
     * 
     * @return Token de acesso no formato "Bearer {token}"
     */
    public String getAccessToken() {
        if (isTokenValid()) {
            log.debug("Using cached token");
            return "Bearer " + cachedToken;
        }
        
        log.info("Requesting new OAuth2 token");
        OAuth2TokenResponse tokenResponse = requestNewToken();
        
        cachedToken = tokenResponse.getAccessToken();
        // Subtrai 60 segundos para garantir que o token seja renovado antes de expirar
        tokenExpirationTime = Instant.now().plusSeconds(tokenResponse.getExpiresIn() - 60);
        
        log.info("New token obtained, expires at: {}", tokenExpirationTime);
        return "Bearer " + cachedToken;
    }
    
    /**
     * Força a renovação do token, ignorando o cache.
     * 
     * @return Token de acesso no formato "Bearer {token}"
     */
    public String refreshToken() {
        log.info("Forcing token refresh");
        cachedToken = null;
        tokenExpirationTime = null;
        return getAccessToken();
    }
    
    private boolean isTokenValid() {
        return cachedToken != null 
            && tokenExpirationTime != null 
            && Instant.now().isBefore(tokenExpirationTime);
    }
    
    private OAuth2TokenResponse requestNewToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", oauth2Properties.getGrantType());
        formData.add("client_id", oauth2Properties.getClientId());
        formData.add("client_secret", oauth2Properties.getClientSecret());
        
        return authRestClient.post()
            .uri(oauth2Properties.getTokenUri())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(formData)
            .retrieve()
            .body(OAuth2TokenResponse.class);
    }
}
