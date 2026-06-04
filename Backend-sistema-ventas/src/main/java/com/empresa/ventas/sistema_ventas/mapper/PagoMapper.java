package com.empresa.ventas.sistema_ventas.mapper;

import com.empresa.ventas.sistema_ventas.dto.response.PagoResponse;
import com.empresa.ventas.sistema_ventas.entity.pago.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PagoMapper {
    @Mapping(source = "metodoPago.codigo", target = "metodoPagoCodigo")
    @Mapping(source = "metodoPago.descripcion", target = "metodoPagoDescripcion")
    @Mapping(source = "estadoPago.codigo", target = "estadoPagoCodigo")
    @Mapping(source = "estadoPago.descripcion", target = "estadoPagoDescripcion")
    PagoResponse toResponse(Pago pago);
}