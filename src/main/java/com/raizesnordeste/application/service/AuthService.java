package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.CadastroRequest;
import com.raizesnordeste.application.dto.request.LoginRequest;
import com.raizesnordeste.application.dto.response.AuthResponse;
import com.raizesnordeste.application.dto.response.UsuarioResponse;
import com.raizesnordeste.domain.enums.Role;
import com.raizesnordeste.domain.model.Usuario;
import com.raizesnordeste.infrastructure.config.AuditService;
import com.raizesnordeste.infrastructure.persistence.repository.UsuarioRepository;
import com.raizesnordeste.infrastructure.security.CustomUserDetailsService;
import com.raizesnordeste.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final AuditService auditService;

    @Transactional
    public UsuarioResponse cadastrar(CadastroRequest request, String roleRequisitante) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + request.getEmail());
        }
        if (request.getCpf() != null && usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        // Apenas ADMIN pode criar perfis não-CLIENTE
        Role role = request.getRole();
        if (role != Role.CLIENTE && !"ROLE_ADMIN".equals(roleRequisitante)) {
            role = Role.CLIENTE;
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .cpf(request.getCpf())
                .telefone(request.getTelefone())
                .role(role)
                .consentimentoLgpd(request.getConsentimentoLgpd())
                .dataConsentimentoLgpd(LocalDateTime.now())
                .consentimentoMarketing(request.isConsentimentoMarketing())
                .ativo(true)
                .pontosFidelidade(0)
                .build();

        usuario = usuarioRepository.save(usuario);
        auditService.registrar(request.getEmail(), "CADASTRO_USUARIO", "USUARIO",
                String.valueOf(usuario.getId()), "Novo usuário cadastrado com role=" + role);

        return toResponse(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        Map<String, Object> claims = Map.of("role", usuario.getRole().name(), "nome", usuario.getNome());
        String token = jwtService.generateToken(userDetails, claims);

        auditService.registrar(request.getEmail(), "LOGIN", "USUARIO",
                String.valueOf(usuario.getId()), "Login realizado com sucesso");

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .role(usuario.getRole())
                .expiracaoMs(86400000L)
                .build();
    }

    public UsuarioResponse toResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .telefone(u.getTelefone())
                .role(u.getRole())
                .ativo(u.isAtivo())
                .pontosFidelidade(u.getPontosFidelidade())
                .consentimentoLgpd(u.isConsentimentoLgpd())
                .consentimentoMarketing(u.isConsentimentoMarketing())
                .criadoEm(u.getCriadoEm())
                .build();
    }
}
