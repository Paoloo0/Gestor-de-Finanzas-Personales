package com.universidad.gestorfinanzas.dto;

import java.math.BigDecimal;

public class RegistroUsuarioDto {

    private String nombre;
    private String correo;
    private BigDecimal ingresoMensual;

    public RegistroUsuarioDto() {
    }

    public RegistroUsuarioDto(String nombre, String correo, BigDecimal ingresoMensual) {
        this.nombre = nombre;
        this.correo = correo;
        this.ingresoMensual = ingresoMensual;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public BigDecimal getIngresoMensual() {
        return ingresoMensual;
    }

    public void setIngresoMensual(BigDecimal ingresoMensual) {
        this.ingresoMensual = ingresoMensual;
    }
}