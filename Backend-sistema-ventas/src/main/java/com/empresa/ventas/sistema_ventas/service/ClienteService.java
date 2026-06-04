package com.empresa.ventas.sistema_ventas.service;

import com.empresa.ventas.sistema_ventas.dto.request.ClienteRequest;
import com.empresa.ventas.sistema_ventas.entity.cliente.Cliente;
import com.empresa.ventas.sistema_ventas.entity.cliente.TipoDocumento;
import com.empresa.ventas.sistema_ventas.exception.ResourceNotFoundException;
import com.empresa.ventas.sistema_ventas.repository.ClienteRepository;
import com.empresa.ventas.sistema_ventas.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public Page<Cliente> listar(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    public Page<Cliente> buscar(String termino, Pageable pageable) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrRazonSocialContainingIgnoreCase(
                termino, termino, termino, pageable);
    }

    public Cliente buscarPorDocumento(Long tipoId, String numero) {
        return clienteRepository.findByTipoDocumentoIdAndNumeroDocumento(tipoId, numero)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "documento", tipoId + ":" + numero));
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
    }

    @Transactional
    public Cliente crear(ClienteRequest request) {
        if (request.getTipoDocumentoId() != null && request.getNumeroDocumento() != null) {
            if (clienteRepository.existsByTipoDocumentoIdAndNumeroDocumento(request.getTipoDocumentoId(), request.getNumeroDocumento())) {
                throw new RuntimeException("Cliente con documento ya registrado");
            }
        }

        TipoDocumento tipoDocumento = null;
        if (request.getTipoDocumentoId() != null) {
            tipoDocumento = tipoDocumentoRepository.findById(request.getTipoDocumentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("TipoDocumento", "id", request.getTipoDocumentoId()));
        }

        Cliente cliente = Cliente.builder()
                .uuid(UUID.randomUUID())
                .tipoDocumento(tipoDocumento)
                .numeroDocumento(request.getNumeroDocumento())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .razonSocial(request.getRazonSocial())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .distrito(request.getDistrito())
                .provincia(request.getProvincia())
                .departamento(request.getDepartamento())
                .activo(true)
                .build();

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarPorId(id);

        if (request.getTipoDocumentoId() != null) {
            TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(request.getTipoDocumentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("TipoDocumento", "id", request.getTipoDocumentoId()));
            cliente.setTipoDocumento(tipoDocumento);
        }

        cliente.setNumeroDocumento(request.getNumeroDocumento());
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setRazonSocial(request.getRazonSocial());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        cliente.setDistrito(request.getDistrito());
        cliente.setProvincia(request.getProvincia());
        cliente.setDepartamento(request.getDepartamento());

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void toggleActivo(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setActivo(!cliente.isActivo());
        clienteRepository.save(cliente);
    }
}