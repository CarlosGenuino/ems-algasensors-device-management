package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.client.ViaCepClient;
import com.algaworks.algasensors.device.management.api.model.CepOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Buscar Cep",
            description = "Busca os detalhes do cep informado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes do cep",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CepOutput.class))),
    })
    @GetMapping(value = "{cep}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CepOutput getCep(@PathVariable String cep){
        return viaCepClient.getCep(cep);
    }
}
