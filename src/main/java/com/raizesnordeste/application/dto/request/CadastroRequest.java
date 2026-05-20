package com.raizesnordeste.application.dto.request;

import com.raizesnordeste.domain.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CadastroRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
    private String cpf;

    private String telefone;

    @NotNull(message = "Consentimento LGPD é obrigatório")
    @AssertTrue(message = "É necessário aceitar os termos de uso e política de privacidade (LGPD)")
    private Boolean consentimentoLgpd;

    private boolean consentimentoMarketing = false;

    // Role padrão é CLIENTE; ADMIN pode criar outros perfis
    private Role role = Role.CLIENTE;
}
