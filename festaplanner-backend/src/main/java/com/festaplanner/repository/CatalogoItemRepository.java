package com.festaplanner.repository;

import com.festaplanner.model.CatalogoItem;
import com.festaplanner.model.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CatalogoItemRepository extends JpaRepository<CatalogoItem, Long>,
        JpaSpecificationExecutor<CatalogoItem> {

    List<CatalogoItem> findByTipoAndAtivoTrue(TipoItem tipo);

    List<CatalogoItem> findByTipoAndCategoriaAndAtivoTrue(TipoItem tipo, String categoria);

    List<CatalogoItem> findByTipoAndEventoTemaAndAtivoTrue(TipoItem tipo, String eventoTema);
}
