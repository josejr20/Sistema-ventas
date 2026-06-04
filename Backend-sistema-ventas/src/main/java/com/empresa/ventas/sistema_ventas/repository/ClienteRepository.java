package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.cliente.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByTipoDocumentoIdAndNumeroDocumento(Long tipoId, String numero);
    Page<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido, Pageable pageable);
    Page<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrRazonSocialContainingIgnoreCase(
            String nombre, String apellido, String razonSocial, Pageable pageable);
    boolean existsByTipoDocumentoIdAndNumeroDocumento(Long tipoId, String numero);
}