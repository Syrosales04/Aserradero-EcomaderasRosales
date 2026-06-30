package com.aserradero.repository;

import com.aserradero.model.FacturaProveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FacturaProveedorRepository extends JpaRepository<FacturaProveedor, Long> {
    List<FacturaProveedor> findByFechaFacturaBetween(LocalDate desde, LocalDate hasta);
    List<FacturaProveedor> findByEstadoAndFechaFacturaBetween(String estado, LocalDate desde, LocalDate hasta);

    /** Sirve para impedir que un mismo ingreso tenga dos facturas no anuladas. */
    boolean existsByIngresoIdAndEstadoNot(Long idIngreso, String estado);
}
