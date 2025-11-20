package com.algaworks.algasensors.device.management.api.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private RestClient authRestClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private OAuth2Properties oauth2Properties;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        oauth2Properties = new OAuth2Properties();
        oauth2Properties.setTokenUri("https://api.example.com/auth/oauth/v2/token");
        oauth2Properties.setClientId("test-client-id");
        oauth2Properties.setClientSecret("test-client-secret");
        oauth2Properties.setGrantType("client_credentials");

        authService = new AuthService(authRestClient, oauth2Properties);
    }

    @Test
    void shouldRequestNewTokenWhenCacheIsEmpty() {
        // Arrange
        OAuth2TokenResponse tokenResponse = createTokenResponse("access-token-123", 3600L);
        setupMockRestClient(tokenResponse);

        // Act
        String result = authService.getAccessToken();

        // Assert
        assertThat(result).isEqualTo("Bearer access-token-123");
        verify(authRestClient, times(1)).post();
    }

    @Test
    void shouldUseCachedTokenWhenValid() {
        // Arrange
        OAuth2TokenResponse tokenResponse = createTokenResponse("cached-token", 3600L);
        setupMockRestClient(tokenResponse);

        // Act - First call to populate cache
        String firstResult = authService.getAccessToken();
        // Act - Second call should use cache
        String secondResult = authService.getAccessToken();

        // Assert
        assertThat(firstResult).isEqualTo("Bearer cached-token");
        assertThat(secondResult).isEqualTo("Bearer cached-token");
        verify(authRestClient, times(1)).post(); // Only called once
    }

    @Test
    void shouldRefreshTokenWhenExpired() throws InterruptedException {
        // Arrange - Token that expires in 1 second
        OAuth2TokenResponse firstToken = createTokenResponse("first-token", 1L);
        OAuth2TokenResponse secondToken = createTokenResponse("second-token", 3600L);

        setupMockRestClient(firstToken);
        String firstResult = authService.getAccessToken();

        // Wait for token to expire (1 second + 60 second buffer means it's already
        // expired)
        Thread.sleep(100); // Small delay to ensure time has passed

        // Setup new token response
        reset(authRestClient);
        setupMockRestClient(secondToken);

        // Act
        String secondResult = authService.getAccessToken();

        // Assert
        assertThat(firstResult).isEqualTo("Bearer first-token");
        assertThat(secondResult).isEqualTo("Bearer second-token");
    }

    @Test
    void shouldForceRefreshTokenWhenRefreshMethodCalled() {
        // Arrange
        OAuth2TokenResponse firstToken = createTokenResponse("first-token", 3600L);
        OAuth2TokenResponse refreshedToken = createTokenResponse("refreshed-token", 3600L);

        setupMockRestClient(firstToken);
        authService.getAccessToken();

        reset(authRestClient);
        setupMockRestClient(refreshedToken);

        // Act
        String result = authService.refreshToken();

        // Assert
        assertThat(result).isEqualTo("Bearer refreshed-token");
        verify(authRestClient, times(1)).post();
    }

    @Test
    void shouldSendCorrectFormDataToOAuth2Server() {
        // Arrange
        OAuth2TokenResponse tokenResponse = createTokenResponse("token", 3600L);
        setupMockRestClient(tokenResponse);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<MultiValueMap<String, String>> bodyCaptor = ArgumentCaptor.forClass(MultiValueMap.class);

        // Act
        authService.getAccessToken();

        // Assert
        verify(requestBodySpec).body(bodyCaptor.capture());
        MultiValueMap<String, String> capturedBody = bodyCaptor.getValue();

        assertThat(capturedBody.getFirst("grant_type")).isEqualTo("client_credentials");
        assertThat(capturedBody.getFirst("client_id")).isEqualTo("test-client-id");
        assertThat(capturedBody.getFirst("client_secret")).isEqualTo("test-client-secret");
    }

    @Test
    void shouldUseCorrectContentTypeForRequest() {
        // Arrange
        OAuth2TokenResponse tokenResponse = createTokenResponse("token", 3600L);
        setupMockRestClient(tokenResponse);

        // Act
        authService.getAccessToken();

        // Assert
        verify(requestBodySpec).contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    void shouldCallCorrectTokenUri() {
        // Arrange
        OAuth2TokenResponse tokenResponse = createTokenResponse("token", 3600L);
        setupMockRestClient(tokenResponse);

        // Act
        authService.getAccessToken();

        // Assert
        verify(requestBodyUriSpec).uri("https://api.example.com/auth/oauth/v2/token");
    }

    @Test
    void shouldReturnBearerFormattedToken() {
        // Arrange
        OAuth2TokenResponse tokenResponse = createTokenResponse("my-access-token", 3600L);
        setupMockRestClient(tokenResponse);

        // Act
        String result = authService.getAccessToken();

        // Assert
        assertThat(result).startsWith("Bearer ");
        assertThat(result).isEqualTo("Bearer my-access-token");
    }

    @Test
    void shouldHandleMultipleConsecutiveCalls() {
        // Arrange
        OAuth2TokenResponse tokenResponse = createTokenResponse("token", 3600L);
        setupMockRestClient(tokenResponse);

        // Act - Multiple consecutive calls
        String result1 = authService.getAccessToken();
        String result2 = authService.getAccessToken();
        String result3 = authService.getAccessToken();

        // Assert - All should return same cached token
        assertThat(result1).isEqualTo("Bearer token");
        assertThat(result2).isEqualTo("Bearer token");
        assertThat(result3).isEqualTo("Bearer token");
        verify(authRestClient, times(1)).post(); // Only one API call
    }

    // Helper methods

    private OAuth2TokenResponse createTokenResponse(String accessToken, Long expiresIn) {
        OAuth2TokenResponse response = new OAuth2TokenResponse();
        response.setAccessToken(accessToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(expiresIn);
        return response;
    }

    private void setupMockRestClient(OAuth2TokenResponse tokenResponse) {
        when(authRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultiValueMap.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(OAuth2TokenResponse.class)).thenReturn(tokenResponse);
    }
}
