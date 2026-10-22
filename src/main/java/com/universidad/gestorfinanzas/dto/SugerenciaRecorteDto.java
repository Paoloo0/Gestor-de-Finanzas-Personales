package com.universidad.gestorfinanzas.dto;

import java.math.BigDecimal;

/**
 * DTO que representa una fila en la tabla de optimización del bloque de Ocio (30%).
 * Contiene exactamente las columnas requeridas:
 * 1. Subcategoría Ocio (30%)
 * 2. Límite Base
 * 3. Gasto Actual (lo que ya se consumió en el mes)
 * 4. Nuevo Límite (el tope real y coherente permitido)
 * 5. Consejo / Diagnóstico financiero
 */
public class SugerenciaRecorteDto {

    private String nombreSubcategoria;
    private BigDecimal limiteBase;
    private BigDecimal gastoActual;
    private BigDecimal nuevoLimite;
    private String consejo;

    public SugerenciaRecorteDto() {
    }

    public SugerenciaRecorteDto(String nombreSubcategoria, BigDecimal limiteBase,
                                BigDecimal gastoActual, BigDecimal nuevoLimite,
                                String consejo) {
        this.nombreSubcategoria = nombreSubcategoria;
        this.limiteBase = limiteBase;
        this.gastoActual = gastoActual;
        this.nuevoLimite = nuevoLimite;
        this.consejo = consejo;
    }

    public String getNombreSubcategoria() {
        return nombreSubcategoria;
    }

    public void setNombreSubcategoria(String nombreSubcategoria) {
        this.nombreSubcategoria = nombreSubcategoria;
    }

    public BigDecimal getLimiteBase() {
        return limiteBase;
    }

    public void setLimiteBase(BigDecimal limiteBase) {
        this.limiteBase = limiteBase;
    }

    public BigDecimal getGastoActual() {
        return gastoActual;
    }

    public void setGastoActual(BigDecimal gastoActual) {
        this.gastoActual = gastoActual;
    }

    public BigDecimal getNuevoLimite() {
        return nuevoLimite;
    }

    public void setNuevoLimite(BigDecimal nuevoLimite) {
        this.nuevoLimite = nuevoLimite;
    }

    public String getConsejo() {
        return consejo;
    }

    public void setConsejo(String consejo) {
        this.consejo = consejo;
    }
}