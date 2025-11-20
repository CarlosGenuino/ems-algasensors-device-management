package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Endpoint para obter o token de acesso atual (com cache).
     * 
     * @return Token de acesso no formato Bearer
     */
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getToken() {
        String token = authService.getAccessToken();
        return ResponseEntity.ok(Map.of("authorization", token));
    }
    
    /**
     * Endpoint para forçar a renovação do token.
     * 
     * @return Novo token de acesso no formato Bearer
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<Map<String, String>> refreshToken() {
        String token = authService.refreshToken();
        return ResponseEntity.ok(Map.of("authorization", token));
    }
}
