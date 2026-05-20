package com.raizesnordeste.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "estoque_itens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"produto_id", "unidade_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstoqueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @Column(name = "quantidade", nullable = false)
    private int quantidade = 0;

    @Column(name = "estoque_minimo", nullable = false)
    private int estoqueMinimo = 5;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    // Regra de negócio: verifica se tem estoque suficiente
    public boolean temEstoque(int qtdNecessaria) {
        return this.quantidade >= qtdNecessaria;
    }

    public void subtrair(int qtd) {
        if (!temEstoque(qtd)) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + produto.getNome());
        }
        this.quantidade -= qtd;
    }

    public void adicionar(int qtd) {
        this.quantidade += qtd;
    }
}
