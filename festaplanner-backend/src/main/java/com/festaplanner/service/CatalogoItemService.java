package com.festaplanner.service;

import com.festaplanner.dto.CatalogoItemDto;
import com.festaplanner.model.CatalogoItem;
import com.festaplanner.model.TipoItem;
import com.festaplanner.exception.ResourceNotFoundException;
import com.festaplanner.repository.CatalogoItemRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cobre todo o catalogo usado no orcamento-component.ts real: buffet,
 * bolo, tema (infantil/15 anos/pasta de evento), docinhos, salgadinhos,
 * bebidas, decoracao modular e musica/animacao - tudo pelo mesmo
 * CRUD generico, filtrado por "tipo" (+ subcategoria/evento quando
 * aplicavel).
 */
@Service
@RequiredArgsConstructor
public class CatalogoItemService {

    private final CatalogoItemRepository repository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<CatalogoItemDto> listarTodos() {
        return repository.findAll().stream().filter(CatalogoItem::isAtivo).map(this::paraDto).toList();
    }

    public List<CatalogoItemDto> listarPorTipo(TipoItem tipo) {
        return repository.findByTipoAndAtivoTrue(tipo).stream().map(this::paraDto).toList();
    }

    public List<CatalogoItemDto> listarPorTipoECategoria(TipoItem tipo, String categoria) {
        return repository.findByTipoAndCategoriaAndAtivoTrue(tipo, categoria).stream().map(this::paraDto).toList();
    }

    /** Temas filtrados por tipo de evento (ex: GET /api/catalogo/temas?evento=Casamento) + genero/categoriaTema opcionais */
    public List<CatalogoItemDto> listarTemas(String evento, String genero, String categoriaTema) {
        Specification<CatalogoItem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tipo"), TipoItem.TEMA));
            predicates.add(cb.isTrue(root.get("ativo")));
            if (evento != null && !evento.isBlank()) {
                predicates.add(cb.equal(root.get("eventoTema"), evento));
            }
            if (genero != null && !genero.isBlank()) {
                predicates.add(cb.equal(root.get("generoTema"), genero));
            }
            if (categoriaTema != null && !categoriaTema.isBlank()) {
                predicates.add(cb.equal(root.get("categoriaTema"), categoriaTema));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec).stream().map(this::paraDto).toList();
    }

    public CatalogoItemDto buscarPorId(Long id) {
        return paraDto(buscarEntidade(id));
    }

    @Transactional
    public CatalogoItemDto criar(CatalogoItemDto dto, MultipartFile imagem) {
        CatalogoItem item = new CatalogoItem();
        aplicarDto(item, dto);
        item.setAtivo(true);
        if (imagem != null && !imagem.isEmpty()) {
            item.setImagemUrl(salvarImagem(imagem));
        }
        return paraDto(repository.save(item));
    }

    @Transactional
    public CatalogoItemDto atualizar(Long id, CatalogoItemDto dto, MultipartFile imagem) {
        CatalogoItem item = buscarEntidade(id);
        aplicarDto(item, dto);
        if (imagem != null && !imagem.isEmpty()) {
            item.setImagemUrl(salvarImagem(imagem));
        }
        return paraDto(repository.save(item));
    }

    @Transactional
    public void remover(Long id) {
        CatalogoItem item = buscarEntidade(id);
        // Soft delete: preserva itens ja usados em orcamentos antigos
        item.setAtivo(false);
        repository.save(item);
    }

    CatalogoItem buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de catalogo nao encontrado: " + id));
    }

    private void aplicarDto(CatalogoItem item, CatalogoItemDto dto) {
        item.setTipo(TipoItem.valueOf(dto.getTipo().toUpperCase()));
        item.setNome(dto.getNome());
        item.setDescricao(dto.getDescricao());
        item.setPreco(dto.getPreco());
        item.setPrecoUnitario(dto.getPrecoUnitario());
        item.setQuantidadeMinima(dto.getQuantidadeMinima());
        item.setIncremento(dto.getIncremento());
        item.setCategoria(dto.getCategoria());
        item.setLinha(dto.getLinha());
        item.setUnidadeMedida(dto.getUnidadeMedida());
        item.setFornecimento(dto.getFornecimento());
        item.setNivelPersonalizacao(dto.getNivelPersonalizacao());
        item.setSobOrcamento(dto.isSobOrcamento());
        item.setPrecoReferencia(dto.getPrecoReferencia());
        item.setDuracaoHoras(dto.getDuracaoHoras());
        item.setItensInclusos(dto.getItensInclusos() != null ? dto.getItensInclusos() : new ArrayList<>());
        item.setItensNaoInclusos(dto.getItensNaoInclusos() != null ? dto.getItensNaoInclusos() : new ArrayList<>());
        item.setEventoTema(dto.getEventoTema());
        item.setGeneroTema(dto.getGeneroTema());
        item.setCategoriaTema(dto.getCategoriaTema());
        item.setObrigatorio(dto.isObrigatorio());
        item.setInclusoNoPacote(dto.isInclusoNoPacote());
        if (dto.getId() != null) {
            item.setAtivo(dto.isAtivo());
        }
    }

    private String salvarImagem(MultipartFile imagem) {
        try {
            Path pasta = Paths.get(uploadDir);
            if (!Files.exists(pasta)) {
                Files.createDirectories(pasta);
            }
            String extensao = "";
            String nomeOriginal = imagem.getOriginalFilename();
            if (nomeOriginal != null && nomeOriginal.contains(".")) {
                extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf('.'));
            }
            String nomeArquivo = UUID.randomUUID() + extensao;
            Path destino = pasta.resolve(nomeArquivo);
            Files.copy(imagem.getInputStream(), destino);
            return "/uploads/" + nomeArquivo;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar a imagem do item de catalogo", e);
        }
    }

    private CatalogoItemDto paraDto(CatalogoItem c) {
        return CatalogoItemDto.builder()
                .id(c.getId())
                .tipo(c.getTipo().name())
                .nome(c.getNome())
                .descricao(c.getDescricao())
                .imagemUrl(c.getImagemUrl())
                .preco(c.getPreco())
                .precoUnitario(c.getPrecoUnitario())
                .quantidadeMinima(c.getQuantidadeMinima())
                .incremento(c.getIncremento())
                .categoria(c.getCategoria())
                .linha(c.getLinha())
                .unidadeMedida(c.getUnidadeMedida())
                .fornecimento(c.getFornecimento())
                .nivelPersonalizacao(c.getNivelPersonalizacao())
                .sobOrcamento(c.isSobOrcamento())
                .precoReferencia(c.getPrecoReferencia())
                .duracaoHoras(c.getDuracaoHoras())
                .itensInclusos(c.getItensInclusos())
                .itensNaoInclusos(c.getItensNaoInclusos())
                .eventoTema(c.getEventoTema())
                .generoTema(c.getGeneroTema())
                .categoriaTema(c.getCategoriaTema())
                .obrigatorio(c.isObrigatorio())
                .inclusoNoPacote(c.isInclusoNoPacote())
                .ativo(c.isAtivo())
                .build();
    }
}
