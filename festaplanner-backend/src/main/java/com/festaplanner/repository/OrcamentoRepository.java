package com.festaplanner.repository;

import com.festaplanner.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long>,
        JpaSpecificationExecutor<Orcamento> {
}
