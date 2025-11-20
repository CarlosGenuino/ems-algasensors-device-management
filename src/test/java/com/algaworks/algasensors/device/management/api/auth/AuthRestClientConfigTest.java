package com.algaworks.algasensors.device.management.api.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRestClientConfigTest {

    @Mock
    private RestClient.Builder mockBuilder;

    @Mock
    private RestClient mockRestClient;

    private OAuth2Properties oauth2Properties;
    private AuthRestClientConfig authRestClientConfig;

    @BeforeEach
    void setUp() {
        oauth2Properties = new OAuth2Properties();
        oauth2Properties.setTokenUri("https://api.dsv.bradseg.com.br:8443/auth/oauth/v2/token");
        oauth2Properties.setClientId("test-client");
        oauth2Properties.setClientSecret("test-secret");

        authRestClientConfig = new AuthRestClientConfig(oauth2Properties);

        // Setup mock builder chain
        when(mockBuilder.baseUrl(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.requestFactory(any(ClientHttpRequestFactory.class))).thenReturn(mockBuilder);
        when(mockBuilder.defaultStatusHandler(any(), any())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockRestClient);
    }

    @Test
    void shouldCreateRestClientWithCorrectBaseUrl() {
        // Act
        authRestClientConfig.authRestClient(mockBuilder);

        // Assert
        verify(mockBuilder).baseUrl("https://api.dsv.bradseg.com.br:8443");
    }

    @Test
    void shouldExtractBaseUrlFromTokenUri() {
        // Arrange
        oauth2Properties.setTokenUri("https://api.example.com:9443/auth/oauth/v2/token");
        authRestClientConfig = new AuthRestClientConfig(oauth2Properties);

        // Act
        authRestClientConfig.authRestClient(mockBuilder);

        // Assert
        verify(mockBuilder).baseUrl("https://api.example.com:9443");
    }

    @Test
    void shouldUseDefaultBaseUrlWhenTokenUriDoesNotContainAuthPath() {
        // Arrange
        oauth2Properties.setTokenUri("https://custom.example.com/token");
        authRestClientConfig = new AuthRestClientConfig(oauth2Properties);

        // Act
        authRestClientConfig.authRestClient(mockBuilder);

        // Assert - When no "/auth" path is found, it uses the fallback default
        verify(mockBuilder).baseUrl("http://authurl.com");
    }

    @Test
    void shouldConfigureRequestFactory() {
        // Act
        authRestClientConfig.authRestClient(mockBuilder);

        // Assert
        verify(mockBuilder).requestFactory(any(ClientHttpRequestFactory.class));
    }

    @Test
    void shouldConfigureErrorHandler() {
        // Act
        authRestClientConfig.authRestClient(mockBuilder);

        // Assert
        verify(mockBuilder).defaultStatusHandler(any(), any());
    }

    @Test
    void shouldReturnRestClientInstance() {
        // Act
        RestClient result = authRestClientConfig.authRestClient(mockBuilder);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockRestClient);
    }

    @Test
    void shouldBuildRestClientWithAllConfigurations() {
        // Act
        authRestClientConfig.authRestClient(mockBuilder);

        // Assert
        verify(mockBuilder).baseUrl(anyString());
        verify(mockBuilder).requestFactory(any(ClientHttpRequestFactory.class));
        verify(mockBuilder).defaultStatusHandler(any(), any());
        verify(mockBuilder).build();
    }
}
