package com.raizesnordeste.application.dto.response;

import com.raizesnordeste.domain.enums.Role;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    // CPF omitido por padrão (LGPD - dado sensível)
    private String telefone;
    private Role role;
    private boolean ativo;
    private int pontosFidelidade;
    private boolean consentimentoLgpd;
    private boolean consentimentoMarketing;
    private LocalDateTime criadoEm;
}
