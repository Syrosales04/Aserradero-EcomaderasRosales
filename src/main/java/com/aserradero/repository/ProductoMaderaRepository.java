package com.aserradero.repository;

import com.aserradero.model.ProductoMadera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoMaderaRepository extends JpaRepository<ProductoMadera, Long> {
    Optional<ProductoMadera> findByTipoMaderaIgnoreCase(String tipoMadera);
}
