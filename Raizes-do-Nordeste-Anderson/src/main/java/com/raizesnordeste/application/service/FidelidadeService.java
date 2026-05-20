package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.response.UsuarioResponse;
import com.raizesnordeste.domain.model.Usuario;
import com.raizesnordeste.infrastructure.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FidelidadeService {

    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Map<String, Object> consultarSaldo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        return Map.of(
                "usuarioId", usuario.getId(),
                "nome", usuario.getNome(),
                "pontos", usuario.getPontosFidelidade(),
                "equivalenteReais", String.format("R$ %.2f", usuario.getPontosFidelidade() / 100.0),
                "regra", "100 pontos = R$1,00 de desconto. Ganhe 1 ponto por R$1,00 gasto."
        );
    }

    @Transactional
    public Map<String, Object> atualizarConsentimento(boolean consentimentoMarketing) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        usuario.setConsentimentoMarketing(consentimentoMarketing);
        usuarioRepository.save(usuario);
        return Map.of(
                "mensagem", "Preferências de comunicação atualizadas",
                "consentimentoMarketing", consentimentoMarketing
        );
    }
}
