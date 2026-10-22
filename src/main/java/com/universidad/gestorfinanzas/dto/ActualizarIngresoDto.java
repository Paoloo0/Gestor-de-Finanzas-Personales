package com.universidad.gestorfinanzas.dto;

import java.math.BigDecimal;

public class ActualizarIngresoDto {

    private BigDecimal nuevoIngreso;

    public ActualizarIngresoDto() {
    }

    public ActualizarIngresoDto(BigDecimal nuevoIngreso) {
        this.nuevoIngreso = nuevoIngreso;
    }

    public BigDecimal getNuevoIngreso() {
        return nuevoIngreso;
    }

    public void setNuevoIngreso(BigDecimal nuevoIngreso) {
        this.nuevoIngreso = nuevoIngreso;
    }
}