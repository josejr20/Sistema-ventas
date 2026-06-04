package com.empresa.ventas.sistema_ventas.service;

import com.empresa.ventas.sistema_ventas.dto.request.ProductoRequest;
import com.empresa.ventas.sistema_ventas.entity.inventario.Inventario;
import com.empresa.ventas.sistema_ventas.entity.inventario.Almacen;
import com.empresa.ventas.sistema_ventas.entity.producto.*;
import com.empresa.ventas.sistema_ventas.exception.ResourceNotFoundException;
import com.empresa.ventas.sistema_ventas.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final AlmacenRepository almacenRepository;
    private final InventarioRepository inventarioRepository;

    public Page<Producto> listar(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public Page<Producto> buscarPorNombre(String nombre, Pageable pageable) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable);
    }

    public Page<Producto> buscarPorCategoria(Long categoriaId, Pageable pageable) {
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId, pageable);
    }

    public Producto buscarPorBarras(String codigoBarras) {
        return productoRepository.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "codigoBarras", codigoBarras));
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    @Transactional
    public Producto crear(ProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", request.getCategoriaId()));

        Marca marca = marcaRepository.findById(request.getMarcaId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca", "id", request.getMarcaId()));

        UnidadMedida unidadMedida = unidadMedidaRepository.findById(request.getUnidadMedidaId())
                .orElseThrow(() -> new ResourceNotFoundException("UnidadMedida", "id", request.getUnidadMedidaId()));

        Producto producto = Producto.builder()
                .uuid(UUID.randomUUID())
                .codigo(request.getCodigo())
                .codigoBarras(request.getCodigoBarras())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(categoria)
                .marca(marca)
                .unidadMedida(unidadMedida)
                .precioCosto(request.getPrecioCosto())
                .precioVenta(request.getPrecioVenta())
                .precioMayoreo(request.getPrecioMayoreo())
                .igvIncluido(request.isIgvIncluido())
                .afectoIgv(request.isAfectoIgv())
                .destacado(request.isDestacado())
                .activo(true)
                .build();

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarPorId(id);

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", request.getCategoriaId()));

        Marca marca = marcaRepository.findById(request.getMarcaId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca", "id", request.getMarcaId()));

        UnidadMedida unidadMedida = unidadMedidaRepository.findById(request.getUnidadMedidaId())
                .orElseThrow(() -> new ResourceNotFoundException("UnidadMedida", "id", request.getUnidadMedidaId()));

        producto.setCategoria(categoria);
        producto.setMarca(marca);
        producto.setUnidadMedida(unidadMedida);
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioCosto(request.getPrecioCosto());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setPrecioMayoreo(request.getPrecioMayoreo());
        producto.setIgvIncluido(request.isIgvIncluido());
        producto.setAfectoIgv(request.isAfectoIgv());
        producto.setDestacado(request.isDestacado());

        return productoRepository.save(producto);
    }

    @Transactional
    public void toggleActivo(Long id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(!producto.isActivo());
        productoRepository.save(producto);
    }
}