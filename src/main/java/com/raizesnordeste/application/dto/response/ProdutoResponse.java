package com.raizesnordeste.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class ProdutoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private boolean disponivel;
    private String imagemUrl;
    private Long unidadeId;
    private String unidadeNome;
    private LocalDateTime criadoEm;
}
