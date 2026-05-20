package com.raizesnordeste.domain.model;

import com.raizesnordeste.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha; // armazenado com BCrypt hash

    @Column(name = "cpf", unique = true)
    private String cpf; // dado sensível - LGPD

    @Column(name = "telefone")
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    // LGPD - Consentimento
    @Column(name = "consentimento_lgpd", nullable = false)
    private boolean consentimentoLgpd = false;

    @Column(name = "data_consentimento_lgpd")
    private LocalDateTime dataConsentimentoLgpd;

    @Column(name = "consentimento_marketing")
    private boolean consentimentoMarketing = false;

    // Fidelidade
    @Column(name = "pontos_fidelidade", nullable = false)
    private int pontosFidelidade = 0;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Pedido> pedidos;
}
