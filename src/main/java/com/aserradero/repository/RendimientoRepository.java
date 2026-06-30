package com.aserradero.repository;

import com.aserradero.model.Rendimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RendimientoRepository extends JpaRepository<Rendimiento, Long> {
    List<Rendimiento> findByFechaCalculoBetween(LocalDate desde, LocalDate hasta);
}
