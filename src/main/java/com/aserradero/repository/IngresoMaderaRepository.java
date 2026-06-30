package com.aserradero.repository;

import com.aserradero.model.IngresoMadera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IngresoMaderaRepository extends JpaRepository<IngresoMadera, Long> {
    List<IngresoMadera> findByFechaIngresoBetween(LocalDate desde, LocalDate hasta);
}
