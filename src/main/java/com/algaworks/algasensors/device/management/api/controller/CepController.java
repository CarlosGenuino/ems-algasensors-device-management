package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.client.ViaCepClient;
import com.algaworks.algasensors.device.management.api.model.CepOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cep")
public class CepController {

    private final ViaCepClient viaCepClient;

    @GetMapping(value = "{cep}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CepOutput getCep(@PathVariable String cep){
        return viaCepClient.getCep(cep);
    }
}
