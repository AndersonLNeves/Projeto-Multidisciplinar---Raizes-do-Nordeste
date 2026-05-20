package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.CadastroRequest;
import com.raizesnordeste.application.dto.request.LoginRequest;
import com.raizesnordeste.application.dto.response.AuthResponse;
import com.raizesnordeste.application.dto.response.UsuarioResponse;
import com.raizesnordeste.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Cadastro e login de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastrar novo usuário", description = "Cria um novo usuário. Requer consentimento LGPD.")
    public ResponseEntity<UsuarioResponse> cadastrar(
            @Valid @RequestBody CadastroRequest request,
            Authentication auth
    ) {
        String roleRequisitante = auth != null
                ? auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("")
                : "";
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrar(request, roleRequisitante));
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica o usuário e retorna JWT Bearer token.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
