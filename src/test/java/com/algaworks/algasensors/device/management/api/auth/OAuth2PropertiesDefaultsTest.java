package com.algaworks.algasensors.device.management.api.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2PropertiesDefaultsTest {

    @Test
    void shouldHaveDefaultGrantType() {
        // Arrange & Act
        OAuth2Properties properties = new OAuth2Properties();

        // Assert
        assertThat(properties.getGrantType()).isEqualTo("client_credentials");
    }

    @Test
    void shouldAllowSettingTokenUri() {
        // Arrange
        OAuth2Properties properties = new OAuth2Properties();

        // Act
        properties.setTokenUri("https://custom.example.com/token");

        // Assert
        assertThat(properties.getTokenUri()).isEqualTo("https://custom.example.com/token");
    }

    @Test
    void shouldAllowSettingClientId() {
        // Arrange
        OAuth2Properties properties = new OAuth2Properties();

        // Act
        properties.setClientId("custom-client-id");

        // Assert
        assertThat(properties.getClientId()).isEqualTo("custom-client-id");
    }

    @Test
    void shouldAllowSettingClientSecret() {
        // Arrange
        OAuth2Properties properties = new OAuth2Properties();

        // Act
        properties.setClientSecret("custom-secret");

        // Assert
        assertThat(properties.getClientSecret()).isEqualTo("custom-secret");
    }

    @Test
    void shouldAllowOverridingGrantType() {
        // Arrange
        OAuth2Properties properties = new OAuth2Properties();

        // Act
        properties.setGrantType("password");

        // Assert
        assertThat(properties.getGrantType()).isEqualTo("password");
    }
}
