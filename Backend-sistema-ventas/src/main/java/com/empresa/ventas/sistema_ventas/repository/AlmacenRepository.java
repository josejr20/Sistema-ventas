package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.inventario.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    Optional<Almacen> findByPrincipalTrueAndActivoTrue();
}