package com.festaplanner.repository;

import com.festaplanner.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByDataInicioBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            LocalDateTime dataFim, LocalDateTime dataInicio);
}
