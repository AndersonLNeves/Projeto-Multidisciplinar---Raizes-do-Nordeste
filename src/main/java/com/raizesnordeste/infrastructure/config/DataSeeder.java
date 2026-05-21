package com.raizesnordeste.infrastructure.config;

import com.raizesnordeste.domain.enums.Role;
import com.raizesnordeste.domain.model.*;
import com.raizesnordeste.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueItemRepository estoqueItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("=== Iniciando seed de dados ===");

        // ---- UNIDADES ----
        Unidade unidade1 = unidadeRepository.save(Unidade.builder()
                .nome("Raízes do Nordeste - Aracaju")
                .endereco("Rua Vila Cristina, 288")
                .cidade("Aracaju").estado("SE")
                .cnpj("1122334000101").telefone("(79) 99999-9991")
                .ativa(true).build());

        Unidade unidade2 = unidadeRepository.save(Unidade.builder()
                .nome("Raízes do Nordeste - Barra dos coqueiros")
                .endereco("Rua Wilson Simonal, 70")
                .cidade("Aracaju").estado("SE")
                .cnpj("1224455000102").telefone("(79) 99999-9992")
                .ativa(true).build());

        // ---- USUÁRIOS ----
        Usuario admin = usuarioRepository.save(Usuario.builder()
                .nome("Admin").email("admin@raizes.com")
                .senha(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .pontosFidelidade(0).build());

        Usuario gerente = usuarioRepository.save(Usuario.builder()
                .nome("Anderson Gerente").email("gerente@raizes.com")
                .senha(passwordEncoder.encode("gerente123"))
                .role(Role.GERENTE).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .pontosFidelidade(0).build());

        usuarioRepository.save(Usuario.builder()
                .nome("Letícia Cozinha").email("cozinha@raizes.com")
                .senha(passwordEncoder.encode("cozinha123"))
                .role(Role.COZINHA).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .pontosFidelidade(0).build());

        usuarioRepository.save(Usuario.builder()
                .nome("Abgail Atendente").email("atendente@raizes.com")
                .senha(passwordEncoder.encode("atendente123"))
                .role(Role.ATENDENTE).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .pontosFidelidade(0).build());

        Usuario cliente = usuarioRepository.save(Usuario.builder()
                .nome("Dudu Cliente").email("cliente@raizes.com")
                .senha(passwordEncoder.encode("cliente123"))
                .cpf("12345678901").telefone("(79) 98765-4321")
                .role(Role.CLIENTE).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .consentimentoMarketing(true)
                .pontosFidelidade(500).build());

        // ---- PRODUTOS (Unidade 1) ----
        Produto tapioca = produtoRepository.save(Produto.builder()
                .nome("Tapioca de Frango com catupiri").descricao("Tapioca artesanal com frango da casa e catupiri")
                .preco(new BigDecimal("12.90")).categoria("LANCHE")
                .unidade(unidade1).disponivel(true).build());

        Produto baiao = produtoRepository.save(Produto.builder()
                .nome("Aratu na Palha").descricao("Carne de aratu (um crustáceo de mangue) temperada e servida assada dentro da palha do coco.")
                .preco(new BigDecimal("25.00")).categoria("PRATO_PRINCIPAL")
                .unidade(unidade1).disponivel(true).build());

        Produto caldo = produtoRepository.save(Produto.builder()
                .nome("Caldo de Sururu").descricao("Preparado com o pequeno molusco local, cozido com bastantes temperos, leite de coco e azeite de dendê.")
                .preco(new BigDecimal("20.00")).categoria("PRATO_PRINCIPAL")
                .unidade(unidade1).disponivel(true).build());

        Produto sucoManga = produtoRepository.save(Produto.builder()
                .nome("Suco de Mangaba").descricao("Suco natural de mangaba 700ml")
                .preco(new BigDecimal("11.90")).categoria("BEBIDA")
                .unidade(unidade1).disponivel(true).build());

        Produto cajuina = produtoRepository.save(Produto.builder()
                .nome("Caju").descricao("Suco natural de Caju 500ml")
                .preco(new BigDecimal("9.50")).categoria("BEBIDA")
                .unidade(unidade1).disponivel(true).build());

        Produto cocada = produtoRepository.save(Produto.builder()
                .nome("Cocada queimada").descricao("Cocada artesanal com leite de coco queimado")
                .preco(new BigDecimal("8.00")).categoria("SOBREMESA")
                .unidade(unidade1).disponivel(true).build());

        // Produto indisponível (para teste)
        Produto carneAssada = produtoRepository.save(Produto.builder()
                .nome("Carne Assada no Bafo").descricao("Produto indisponível no Momento")
                .preco(new BigDecimal("42.90")).categoria("PRATO_PRINCIPAL")
                .unidade(unidade1).disponivel(false).build());

        // ---- PRODUTOS (Unidade 2) ----
        Produto combo1 = produtoRepository.save(Produto.builder()
                .nome("Mata Fome Nordestino").descricao("Carne de Sol + Suco ou Refri + Cocada Queimada")
                .preco(new BigDecimal("49.90")).categoria("COMBO")
                .unidade(unidade2).disponivel(true).build());

        Produto bucho = produtoRepository.save(Produto.builder()
                .nome("Buchada + Feijoada").descricao("A feijoada sergipana é uma variação do tradicional prato brasileiro diferente dos demais estados. e com uma buchada da casa")
                .preco(new BigDecimal("48.00")).categoria("PRATO_PRINCIPAL")
                .unidade(unidade2).disponivel(true).build());

        // ---- ESTOQUE (Unidade 1) ----
        criarEstoque(tapioca, unidade1, 80, 10);
        criarEstoque(baiao, unidade1, 30, 5);
        criarEstoque(caldo, unidade1, 20, 5);
        criarEstoque(sucoManga, unidade1, 100, 20);
        criarEstoque(cajuina, unidade1, 80, 15);
        criarEstoque(cocada, unidade1, 40, 10);
        criarEstoque(carneAssada, unidade1, 0, 5); // sem estoque propositalmente

        // ---- ESTOQUE (Unidade 2) ----
        criarEstoque(combo1, unidade2, 25, 5);
        criarEstoque(bucho, unidade2, 15, 3);

        log.info("=== Seed concluído! ===");
        log.info("Usuários criados:");
        log.info("  ADMIN:     admin@raizes.com / admin123");
        log.info("  GERENTE:   gerente@raizes.com / gerente123");
        log.info("  COZINHA:   cozinha@raizes.com / cozinha123");
        log.info("  ATENDENTE: atendente@raizes.com / atendente123");
        log.info("  CLIENTE:   cliente@raizes.com / cliente123 (500 pontos)");
        log.info("Unidades criadas: {} e {}", unidade1.getId(), unidade2.getId());
        log.info("Swagger: http://localhost:8080/swagger-ui.html");
        log.info("H2 Console: http://localhost:8080/h2-console");
    }

    private void criarEstoque(Produto produto, Unidade unidade, int qtd, int minimo) {
        estoqueItemRepository.save(EstoqueItem.builder()
                .produto(produto).unidade(unidade)
                .quantidade(qtd).estoqueMinimo(minimo)
                .build());
    }
}
