package com.festaplanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festaplanner.dto.CatalogoItemDto;
import com.festaplanner.model.TipoItem;
import com.festaplanner.service.CatalogoItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Catalogo unificado: buffet, bolo, tema, doce, salgadinho, bebida,
 * decoracao e musica/animacao (ver CatalogoItem/TipoItem). Leitura e
 * publica (usada pelo fluxo de orcamento do cliente); escrita exige JWT
 * de admin (telas Catalogo / Novo Produto).
 *
 * IMPORTANTE (ver README): hoje o orcamento-component.ts do Angular NAO
 * consome esses endpoints - os dados estao hardcoded no proprio
 * componente. Este catalogo foi feito para que o front possa migrar para
 * cá e o admin realmente controle o que aparece no orcamento.
 */
@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoItemService catalogoItemService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<CatalogoItemDto>> listarTodos() {
        return ResponseEntity.ok(catalogoItemService.listarTodos());
    }

    /** Ex: GET /api/catalogo/tipo/DOCE ou /api/catalogo/tipo/BUFFET */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<CatalogoItemDto>> listarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(catalogoItemService.listarPorTipo(TipoItem.valueOf(tipo.toUpperCase())));
    }

    /** Ex: GET /api/catalogo/tipo/DOCE/categoria/gourmet */
    @GetMapping("/tipo/{tipo}/categoria/{categoria}")
    public ResponseEntity<List<CatalogoItemDto>> listarPorTipoECategoria(@PathVariable String tipo,
                                                                          @PathVariable String categoria) {
        return ResponseEntity.ok(catalogoItemService.listarPorTipoECategoria(
                TipoItem.valueOf(tipo.toUpperCase()), categoria));
    }

    /** Ex: GET /api/catalogo/temas?evento=Casamento  ou  ?evento=Infantil&genero=menina */
    @GetMapping("/temas")
    public ResponseEntity<List<CatalogoItemDto>> listarTemas(
            @RequestParam(required = false) String evento,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String categoriaTema) {
        return ResponseEntity.ok(catalogoItemService.listarTemas(evento, genero, categoriaTema));
    }

    /** Compatibilidade com o contrato original documentado em catalogo-service.ts */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<CatalogoItemDto>> listarPorCategoriaLegado(@PathVariable String categoria) {
        return ResponseEntity.ok(catalogoItemService.listarTodos().stream()
                .filter(i -> categoria.equalsIgnoreCase(i.getCategoria()))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogoItemDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoItemService.buscarPorId(id));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CatalogoItemDto> criar(@RequestPart("item") String itemJson,
                                                  @RequestPart(value = "imagem", required = false) MultipartFile imagem)
            throws IOException {
        CatalogoItemDto dto = objectMapper.readValue(itemJson, CatalogoItemDto.class);
        return ResponseEntity.ok(catalogoItemService.criar(dto, imagem));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<CatalogoItemDto> atualizar(@PathVariable Long id,
                                                       @RequestPart("item") String itemJson,
                                                       @RequestPart(value = "imagem", required = false) MultipartFile imagem)
            throws IOException {
        CatalogoItemDto dto = objectMapper.readValue(itemJson, CatalogoItemDto.class);
        return ResponseEntity.ok(catalogoItemService.atualizar(id, dto, imagem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        catalogoItemService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
