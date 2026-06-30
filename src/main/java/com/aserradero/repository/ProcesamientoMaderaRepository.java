package com.aserradero.repository;

import com.aserradero.model.ProcesamientoMadera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProcesamientoMaderaRepository extends JpaRepository<ProcesamientoMadera, Long> {

    List<ProcesamientoMadera> findByEstadoAndFechaBetween(String estado, LocalDate desde, LocalDate hasta);
}