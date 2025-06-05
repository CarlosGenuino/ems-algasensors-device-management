package com.algaworks.algasensors.device.management.api.client;

import com.algaworks.algasensors.device.management.api.model.CepOutput;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/ws")
public interface ViaCepClient {

    @GetExchange("/{cep}/json")
    CepOutput getCep(@PathVariable String cep);
}
