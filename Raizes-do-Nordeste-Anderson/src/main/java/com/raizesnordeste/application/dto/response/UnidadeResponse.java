package com.raizesnordeste.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class UnidadeResponse {
    private Long id;
    private String nome;
    private String endereco;
    private String cidade;
    private String estado;
    private String cnpj;
    private String telefone;
    private boolean ativa;
    private LocalDateTime criadoEm;
}
