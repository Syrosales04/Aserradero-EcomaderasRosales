package com.aserradero.repository;

import com.aserradero.model.FacturaCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FacturaClienteRepository extends JpaRepository<FacturaCliente, Long> {
    List<FacturaCliente> findByFechaFacturaBetween(LocalDate desde, LocalDate hasta);
    List<FacturaCliente> findByEstadoAndFechaFacturaBetween(String estado, LocalDate desde, LocalDate hasta);
}
