package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.UnidadeRequest;
import com.raizesnordeste.application.dto.response.UnidadeResponse;
import com.raizesnordeste.domain.model.Unidade;
import com.raizesnordeste.infrastructure.persistence.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    @Transactional
    public UnidadeResponse criar(UnidadeRequest request) {
        Unidade unidade = Unidade.builder()
                .nome(request.getNome())
                .endereco(request.getEndereco())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .cnpj(request.getCnpj())
                .telefone(request.getTelefone())
                .ativa(true)
                .build();
        return toResponse(unidadeRepository.save(unidade));
    }

    @Transactional(readOnly = true)
    public List<UnidadeResponse> listarAtivas() {
        return unidadeRepository.findByAtivaTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UnidadeResponse buscarPorId(Long id) {
        return toResponse(unidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada: " + id)));
    }

    @Transactional
    public UnidadeResponse atualizar(Long id, UnidadeRequest request) {
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada: " + id));
        unidade.setNome(request.getNome());
        unidade.setEndereco(request.getEndereco());
        unidade.setCidade(request.getCidade());
        unidade.setEstado(request.getEstado());
        unidade.setCnpj(request.getCnpj());
        unidade.setTelefone(request.getTelefone());
        return toResponse(unidadeRepository.save(unidade));
    }

    public UnidadeResponse toResponse(Unidade u) {
        return UnidadeResponse.builder()
                .id(u.getId())
                .nome(u.getNome())
                .endereco(u.getEndereco())
                .cidade(u.getCidade())
                .estado(u.getEstado())
                .cnpj(u.getCnpj())
                .telefone(u.getTelefone())
                .ativa(u.isAtiva())
                .criadoEm(u.getCriadoEm())
                .build();
    }
}
