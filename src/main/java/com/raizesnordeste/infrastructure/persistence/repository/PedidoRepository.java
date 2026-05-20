package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.domain.enums.CanalPedido;
import com.raizesnordeste.domain.enums.StatusPedido;
import com.raizesnordeste.domain.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Page<Pedido> findByUsuarioId(Long usuarioId, Pageable pageable);
    Page<Pedido> findByUnidadeId(Long unidadeId, Pageable pageable);
    Page<Pedido> findByCanalPedido(CanalPedido canalPedido, Pageable pageable);
    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);
    Page<Pedido> findByUnidadeIdAndCanalPedido(Long unidadeId, CanalPedido canalPedido, Pageable pageable);
    Page<Pedido> findByUnidadeIdAndStatus(Long unidadeId, StatusPedido status, Pageable pageable);
}
