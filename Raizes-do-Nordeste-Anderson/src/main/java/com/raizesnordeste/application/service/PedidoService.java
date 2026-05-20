package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.ItemPedidoRequest;
import com.raizesnordeste.application.dto.request.PedidoRequest;
import com.raizesnordeste.application.dto.response.ItemPedidoResponse;
import com.raizesnordeste.application.dto.response.PedidoResponse;
import com.raizesnordeste.domain.enums.CanalPedido;
import com.raizesnordeste.domain.enums.StatusPedido;
import com.raizesnordeste.domain.enums.TipoMovimentacao;
import com.raizesnordeste.domain.model.*;
import com.raizesnordeste.infrastructure.config.AuditService;
import com.raizesnordeste.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueItemRepository estoqueItemRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final AuditService auditService;

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Unidade unidade = unidadeRepository.findById(request.getUnidadeId())
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada: " + request.getUnidadeId()));

        if (!unidade.isAtiva()) {
            throw new IllegalStateException("Unidade inativa");
        }

        // Validar pontos de resgate
        int pontosResgatar = request.getPontosParaResgatar();
        if (pontosResgatar > 0 && usuario.getPontosFidelidade() < pontosResgatar) {
            throw new IllegalArgumentException("Pontos insuficientes. Disponível: " + usuario.getPontosFidelidade());
        }

        // Construir itens e validar estoque
        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemPedidoRequest itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemReq.getProdutoId()));

            if (!produto.isDisponivel()) {
                throw new IllegalStateException("Produto indisponível: " + produto.getNome());
            }

            if (!produto.getUnidade().getId().equals(unidade.getId())) {
                throw new IllegalArgumentException("Produto " + produto.getNome() + " não pertence à unidade selecionada");
            }

            // Verificar estoque
            EstoqueItem estoque = estoqueItemRepository
                    .findByProdutoIdAndUnidadeId(produto.getId(), unidade.getId())
                    .orElseThrow(() -> new IllegalStateException("Produto sem estoque configurado: " + produto.getNome()));

            if (!estoque.temEstoque(itemReq.getQuantidade())) {
                throw new IllegalStateException(
                        "Estoque insuficiente para '" + produto.getNome() +
                        "'. Disponível: " + estoque.getQuantidade() +
                        ", Solicitado: " + itemReq.getQuantidade()
                );
            }

            BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(itemReq.getQuantidade()));
            valorTotal = valorTotal.add(subtotal);

            ItemPedido item = ItemPedido.builder()
                    .produto(produto)
                    .quantidade(itemReq.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .subtotal(subtotal)
                    .observacoes(itemReq.getObservacoes())
                    .build();
            itens.add(item);
        }

        // Desconto por pontos: 100 pontos = R$1,00
        BigDecimal descontoPontos = BigDecimal.valueOf(pontosResgatar / 100.0);
        valorTotal = valorTotal.subtract(descontoPontos).max(BigDecimal.ZERO);

        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .unidade(unidade)
                .canalPedido(request.getCanalPedido())
                .status(StatusPedido.PENDENTE)
                .valorTotal(valorTotal)
                .observacoes(request.getObservacoes())
                .pontosUtilizados(pontosResgatar)
                .build();

        // Associar itens ao pedido
        for (ItemPedido item : itens) {
            item.setPedido(pedido);
        }
        pedido.getItens().addAll(itens);

        pedido = pedidoRepository.save(pedido);

        // Baixar estoque e registrar movimentações
        for (ItemPedido item : pedido.getItens()) {
            EstoqueItem estoque = estoqueItemRepository
                    .findByProdutoIdAndUnidadeId(item.getProduto().getId(), unidade.getId()).get();
            estoque.subtrair(item.getQuantidade());
            estoqueItemRepository.save(estoque);

            movimentacaoRepository.save(MovimentacaoEstoque.builder()
                    .produto(item.getProduto())
                    .unidade(unidade)
                    .tipo(TipoMovimentacao.SAIDA)
                    .quantidade(item.getQuantidade())
                    .motivo("VENDA")
                    .referenciaPedidoId(pedido.getId())
                    .build());
        }

        // Debitar pontos
        if (pontosResgatar > 0) {
            usuario.setPontosFidelidade(usuario.getPontosFidelidade() - pontosResgatar);
            usuarioRepository.save(usuario);
        }

        auditService.registrar(email, "CRIAR_PEDIDO", "PEDIDO",
                String.valueOf(pedido.getId()),
                "Pedido criado - canal=" + request.getCanalPedido() + " valor=" + valorTotal);

        return toResponse(pedido);
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

        if (!pedido.podeAvancar() && novoStatus != StatusPedido.CANCELADO) {
            throw new IllegalStateException("Pedido já finalizado. Status atual: " + pedido.getStatus());
        }

        if (novoStatus == StatusPedido.CANCELADO && !pedido.podeCancelar()) {
            throw new IllegalStateException("Não é possível cancelar pedido com status: " + pedido.getStatus());
        }

        // Se cancelou, devolver estoque
        if (novoStatus == StatusPedido.CANCELADO && pedido.getStatus() == StatusPedido.PENDENTE) {
            devolverEstoque(pedido);
        }

        StatusPedido statusAnterior = pedido.getStatus();
        pedido.setStatus(novoStatus);

        // Creditar pontos quando entregue
        if (novoStatus == StatusPedido.ENTREGUE) {
            int pontos = pedido.calcularPontos();
            pedido.setPontosGerados(pontos);
            Usuario usuario = pedido.getUsuario();
            usuario.setPontosFidelidade(usuario.getPontosFidelidade() + pontos);
            usuarioRepository.save(usuario);
        }

        pedido = pedidoRepository.save(pedido);

        auditService.registrar(email, "ATUALIZAR_STATUS_PEDIDO", "PEDIDO",
                String.valueOf(pedidoId),
                "Status: " + statusAnterior + " -> " + novoStatus);

        return toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return toResponse(pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id)));
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(CanalPedido canal, StatusPedido status, Long unidadeId, Pageable pageable) {
        Page<Pedido> pedidos;
        if (canal != null && unidadeId != null) {
            pedidos = pedidoRepository.findByUnidadeIdAndCanalPedido(unidadeId, canal, pageable);
        } else if (canal != null) {
            pedidos = pedidoRepository.findByCanalPedido(canal, pageable);
        } else if (status != null && unidadeId != null) {
            pedidos = pedidoRepository.findByUnidadeIdAndStatus(unidadeId, status, pageable);
        } else if (unidadeId != null) {
            pedidos = pedidoRepository.findByUnidadeId(unidadeId, pageable);
        } else if (status != null) {
            pedidos = pedidoRepository.findByStatus(status, pageable);
        } else {
            pedidos = pedidoRepository.findAll(pageable);
        }
        return pedidos.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listarMeusPedidos(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        return pedidoRepository.findByUsuarioId(usuario.getId(), pageable).map(this::toResponse);
    }

    private void devolverEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            estoqueItemRepository
                    .findByProdutoIdAndUnidadeId(item.getProduto().getId(), pedido.getUnidade().getId())
                    .ifPresent(estoque -> {
                        estoque.adicionar(item.getQuantidade());
                        estoqueItemRepository.save(estoque);
                        movimentacaoRepository.save(MovimentacaoEstoque.builder()
                                .produto(item.getProduto())
                                .unidade(pedido.getUnidade())
                                .tipo(TipoMovimentacao.ENTRADA)
                                .quantidade(item.getQuantidade())
                                .motivo("CANCELAMENTO")
                                .referenciaPedidoId(pedido.getId())
                                .build());
                    });
        }
    }

    public PedidoResponse toResponse(Pedido p) {
        List<ItemPedidoResponse> itensResp = p.getItens().stream().map(i ->
                ItemPedidoResponse.builder()
                        .id(i.getId())
                        .produtoId(i.getProduto().getId())
                        .produtoNome(i.getProduto().getNome())
                        .quantidade(i.getQuantidade())
                        .precoUnitario(i.getPrecoUnitario())
                        .subtotal(i.getSubtotal())
                        .observacoes(i.getObservacoes())
                        .build()
        ).toList();

        return PedidoResponse.builder()
                .id(p.getId())
                .usuarioId(p.getUsuario().getId())
                .usuarioNome(p.getUsuario().getNome())
                .unidadeId(p.getUnidade().getId())
                .unidadeNome(p.getUnidade().getNome())
                .canalPedido(p.getCanalPedido())
                .status(p.getStatus())
                .valorTotal(p.getValorTotal())
                .observacoes(p.getObservacoes())
                .pontosGerados(p.getPontosGerados())
                .pontosUtilizados(p.getPontosUtilizados())
                .itens(itensResp)
                .criadoEm(p.getCriadoEm())
                .atualizadoEm(p.getAtualizadoEm())
                .build();
    }
}
