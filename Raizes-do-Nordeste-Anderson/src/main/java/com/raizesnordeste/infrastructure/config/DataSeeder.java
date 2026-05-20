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
                .nome("Raízes do Nordeste - Centro")
                .endereco("Rua do Forró, 123")
                .cidade("Fortaleza").estado("CE")
                .cnpj("12345678000101").telefone("(85) 99999-0001")
                .ativa(true).build());

        Unidade unidade2 = unidadeRepository.save(Unidade.builder()
                .nome("Raízes do Nordeste - Shopping")
                .endereco("Av. Washington Soares, 800 - Loja 42")
                .cidade("Fortaleza").estado("CE")
                .cnpj("12345678000202").telefone("(85) 99999-0002")
                .ativa(true).build());

        // ---- USUÁRIOS ----
        Usuario admin = usuarioRepository.save(Usuario.builder()
                .nome("Admin").email("admin@raizes.com")
                .senha(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .pontosFidelidade(0).build());

        Usuario gerente = usuarioRepository.save(Usuario.builder()
                .nome("Maria Gerente").email("gerente@raizes.com")
                .senha(passwordEncoder.encode("gerente123"))
                .role(Role.GERENTE).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .pontosFidelidade(0).build());

        usuarioRepository.save(Usuario.builder()
                .nome("João Cozinha").email("cozinha@raizes.com")
                .senha(passwordEncoder.encode("cozinha123"))
                .role(Role.COZINHA).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .pontosFidelidade(0).build());

        usuarioRepository.save(Usuario.builder()
                .nome("Ana Atendente").email("atendente@raizes.com")
                .senha(passwordEncoder.encode("atendente123"))
                .role(Role.ATENDENTE).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .pontosFidelidade(0).build());

        Usuario cliente = usuarioRepository.save(Usuario.builder()
                .nome("Pedro Cliente").email("cliente@raizes.com")
                .senha(passwordEncoder.encode("cliente123"))
                .cpf("12345678901").telefone("(85) 98888-1234")
                .role(Role.CLIENTE).ativo(true)
                .consentimentoLgpd(true).dataConsentimentoLgpd(LocalDateTime.now())
                .consentimentoMarketing(true)
                .pontosFidelidade(500).build());

        // ---- PRODUTOS (Unidade 1) ----
        Produto tapioca = produtoRepository.save(Produto.builder()
                .nome("Tapioca de Queijo Coalho").descricao("Tapioca artesanal com queijo coalho grelhado")
                .preco(new BigDecimal("18.90")).categoria("LANCHE")
                .unidade(unidade1).disponivel(true).build());

        Produto baiao = produtoRepository.save(Produto.builder()
                .nome("Baião de Dois").descricao("Feijão verde com arroz, queijo, bacon e manteiga de garrafa")
                .preco(new BigDecimal("32.50")).categoria("PRATO_PRINCIPAL")
                .unidade(unidade1).disponivel(true).build());

        Produto caldo = produtoRepository.save(Produto.builder()
                .nome("Caldo de Mocotó").descricao("Caldo tradicional nordestino com temperos da casa")
                .preco(new BigDecimal("24.00")).categoria("PRATO_PRINCIPAL")
                .unidade(unidade1).disponivel(true).build());

        Produto sucoManga = produtoRepository.save(Produto.builder()
                .nome("Suco de Manga").descricao("Suco natural de manga 500ml")
                .preco(new BigDecimal("9.90")).categoria("BEBIDA")
                .unidade(unidade1).disponivel(true).build());

        Produto cajuina = produtoRepository.save(Produto.builder()
                .nome("Cajuína").descricao("Cajuína artesanal do Piauí 300ml")
                .preco(new BigDecimal("7.50")).categoria("BEBIDA")
                .unidade(unidade1).disponivel(true).build());

        Produto cocada = produtoRepository.save(Produto.builder()
                .nome("Cocada Branca").descricao("Cocada artesanal com leite de coco")
                .preco(new BigDecimal("6.00")).categoria("SOBREMESA")
                .unidade(unidade1).disponivel(true).build());

        // Produto indisponível (para teste)
        Produto carneAssada = produtoRepository.save(Produto.builder()
                .nome("Carne Assada com Baião").descricao("Produto temporariamente indisponível")
                .preco(new BigDecimal("45.00")).categoria("PRATO_PRINCIPAL")
                .unidade(unidade1).disponivel(false).build());

        // ---- PRODUTOS (Unidade 2) ----
        Produto combo1 = produtoRepository.save(Produto.builder()
                .nome("Combo Nordestino").descricao("Tapioca + Suco + Cocada")
                .preco(new BigDecimal("29.90")).categoria("COMBO")
                .unidade(unidade2).disponivel(true).build());

        Produto bucho = produtoRepository.save(Produto.builder()
                .nome("Buchada de Bode").descricao("Prato tradicional com temperos nordestinos")
                .preco(new BigDecimal("38.00")).categoria("PRATO_PRINCIPAL")
                .unidade(unidade2).disponivel(true).build());

        // ---- ESTOQUE (Unidade 1) ----
        criarEstoque(tapioca, unidade1, 50, 10);
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
