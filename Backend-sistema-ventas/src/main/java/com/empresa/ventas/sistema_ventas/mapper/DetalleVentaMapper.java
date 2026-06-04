package com.empresa.ventas.sistema_ventas.mapper;

import com.empresa.ventas.sistema_ventas.dto.response.DetalleVentaResponse;
import com.empresa.ventas.sistema_ventas.entity.venta.DetalleVenta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DetalleVentaMapper {
    @Mapping(source = "producto.nombre", target = "productoNombre")
    @Mapping(source = "producto.codigo", target = "productoCodigo")
    DetalleVentaResponse toResponse(DetalleVenta detalle);

    List<DetalleVentaResponse> toResponseList(List<DetalleVenta> detalles);
}