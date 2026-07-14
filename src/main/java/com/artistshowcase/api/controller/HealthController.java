package com.artistshowcase.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Verificação de saúde da aplicação")
public class HealthController {

    @GetMapping("/api/health")
    @Operation(
            summary = "Health check",
            description = "Verifica se a aplicação está respondendo"
    )
    public String health() {
        return "Backend funcionando!";
    }
}