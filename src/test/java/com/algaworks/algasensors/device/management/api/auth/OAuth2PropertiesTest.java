package com.algaworks.algasensors.device.management.api.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2PropertiesTest {

    @Test
    void shouldSetAndGetTokenUri() {
        // Arrange
        OAuth2Properties properties = new OAuth2Properties();

        // Act
        properties.setTokenUri("https://test.example.com/oauth/token");

        // Assert
        assertThat(properties.getTokenUri())
                .isEqualTo("https://test.example.com/oauth/token");
    }

    @Test
    void shouldSetAndGetClientId() {
        // Arrange
        OAuth2Properties properties = new OAuth2Properties();

        // Act
        properties.setClientId("test-id");

        // Assert
        assertThat(properties.getClientId())
                .isEqualTo("test-id");
    }

    @Test
    void shouldSetAndGetClientSecret() {
        // Arrange
        OAuth2Properties properties = new OAuth2Properties();

        // Act
        properties.setClientSecret("test-secret");

        // Assert
        assertThat(properties.getClientSecret())
                .isEqualTo("test-secret");
    }

    @Test
    void shouldSetAndGetGrantType() {
        // Arrange
        OAuth2Properties properties = new OAuth2Properties();

        // Act
        properties.setGrantType("client_credentials");

        // Assert
        assertThat(properties.getGrantType())
                .isEqualTo("client_credentials");
    }

    @Test
    void shouldHaveDefaultGrantType() {
        // Arrange & Act
        OAuth2Properties properties = new OAuth2Properties();

        // Assert
        assertThat(properties.getGrantType()).isEqualTo("client_credentials");
    }
}
