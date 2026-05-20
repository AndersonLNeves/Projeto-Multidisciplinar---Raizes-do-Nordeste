package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.service.FidelidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fidelidade")
@RequiredArgsConstructor
@Tag(name = "Fidelidade", description = "Programa de pontos e fidelização")
@SecurityRequirement(name = "bearerAuth")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    @GetMapping("/saldo")
    @Operation(summary = "Consultar saldo de pontos do usuário logado",
            description = "100 pontos = R$1,00 de desconto. Ganhe 1 ponto por R$1,00 gasto.")
    public ResponseEntity<Map<String, Object>> saldo() {
        return ResponseEntity.ok(fidelidadeService.consultarSaldo());
    }

    @PatchMapping("/consentimento")
    @Operation(summary = "Atualizar consentimento de marketing (LGPD)")
    public ResponseEntity<Map<String, Object>> consentimento(
            @RequestParam boolean consentimentoMarketing
    ) {
        return ResponseEntity.ok(fidelidadeService.atualizarConsentimento(consentimentoMarketing));
    }
}
