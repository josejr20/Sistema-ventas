package com.empresa.ventas.sistema_ventas.mapper;

import com.empresa.ventas.sistema_ventas.dto.response.VentaResponse;
import com.empresa.ventas.sistema_ventas.entity.venta.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DetalleVentaMapper.class})
public interface VentaMapper {
    @Mapping(source = "usuario.nombre", target = "vendedorNombre")
    @Mapping(source = "usuario.apellido", target = "vendedorApellido")
    @Mapping(source = "estadoVenta.codigo", target = "estadoCodigo")
    @Mapping(source = "estadoVenta.descripcion", target = "estadoDescripcion")
    VentaResponse toResponse(Venta venta);
}