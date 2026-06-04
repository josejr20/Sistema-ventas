package com.empresa.ventas.sistema_ventas.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String recurso, String campo, Object valor) {
        super(recurso + " no encontrado con " + campo + ": " + valor);
    }
}
