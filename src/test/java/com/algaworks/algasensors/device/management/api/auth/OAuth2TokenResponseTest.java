package com.algaworks.algasensors.device.management.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2TokenResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializeFromJson() throws Exception {
        // Arrange
        String json = """
                {
                    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
                    "token_type": "Bearer",
                    "expires_in": 3600,
                    "scope": "read write"
                }
                """;

        // Act
        OAuth2TokenResponse response = objectMapper.readValue(json, OAuth2TokenResponse.class);

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        assertThat(response.getScope()).isEqualTo("read write");
    }

    @Test
    void shouldDeserializeWithoutOptionalScope() throws Exception {
        // Arrange
        String json = """
                {
                    "access_token": "token123",
                    "token_type": "Bearer",
                    "expires_in": 7200
                }
                """;

        // Act
        OAuth2TokenResponse response = objectMapper.readValue(json, OAuth2TokenResponse.class);

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("token123");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(7200L);
        assertThat(response.getScope()).isNull();
    }

    @Test
    void shouldSerializeToJson() throws Exception {
        // Arrange
        OAuth2TokenResponse response = new OAuth2TokenResponse();
        response.setAccessToken("my-token");
        response.setTokenType("Bearer");
        response.setExpiresIn(1800L);
        response.setScope("api");

        // Act
        String json = objectMapper.writeValueAsString(response);

        // Assert
        assertThat(json).contains("\"access_token\":\"my-token\"");
        assertThat(json).contains("\"token_type\":\"Bearer\"");
        assertThat(json).contains("\"expires_in\":1800");
        assertThat(json).contains("\"scope\":\"api\"");
    }

    @Test
    void shouldHandleSnakeCasePropertyNames() throws Exception {
        // Arrange - JSON with snake_case (standard OAuth2 format)
        String json = """
                {
                    "access_token": "abc123",
                    "token_type": "Bearer",
                    "expires_in": 600
                }
                """;

        // Act
        OAuth2TokenResponse response = objectMapper.readValue(json, OAuth2TokenResponse.class);

        // Assert - Should map to camelCase Java properties
        assertThat(response.getAccessToken()).isEqualTo("abc123");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(600L);
    }

    @Test
    void shouldSetAndGetAllProperties() {
        // Arrange
        OAuth2TokenResponse response = new OAuth2TokenResponse();

        // Act
        response.setAccessToken("test-token");
        response.setTokenType("Bearer");
        response.setExpiresIn(3600L);
        response.setScope("read");

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("test-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        assertThat(response.getScope()).isEqualTo("read");
    }
}
