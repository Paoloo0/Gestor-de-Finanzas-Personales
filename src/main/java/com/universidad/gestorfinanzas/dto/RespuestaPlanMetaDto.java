package com.universidad.gestorfinanzas.dto;

import java.math.BigDecimal;
import java.util.List;

public class RespuestaPlanMetaDto {

    private String nombreMeta;
    private BigDecimal montoNecesario;
    private int plazoMeses;
    private BigDecimal cuotaMensual;
    private BigDecimal ingresoMensual;
    private BigDecimal presupuestoAhorro20;
    private BigDecimal presupuestoOcio30;
    private BigDecimal gastoActualOcio30;
    private BigDecimal nuevoPresupuestoOcioSugerido;
    private boolean esFactible;
    private String mensajeDiagnostico;
    private String mensajeProteccionNecesidades;
    private List<SugerenciaRecorteDto> recortesSugeridos;

    // Campos de propuesta y adaptación de plazos
    private Integer plazoSugeridoViable;
    private BigDecimal cuotaViableSugerida;
    private String propuestaExtensionPlazo;

    public RespuestaPlanMetaDto() {
    }

    public RespuestaPlanMetaDto(String nombreMeta, BigDecimal montoNecesario, int plazoMeses,
                                BigDecimal cuotaMensual, BigDecimal ingresoMensual,
                                BigDecimal presupuestoAhorro20, BigDecimal presupuestoOcio30,
                                BigDecimal gastoActualOcio30, BigDecimal nuevoPresupuestoOcioSugerido,
                                boolean esFactible, String mensajeDiagnostico,
                                String mensajeProteccionNecesidades, List<SugerenciaRecorteDto> recortesSugeridos,
                                Integer plazoSugeridoViable, BigDecimal cuotaViableSugerida,
                                String propuestaExtensionPlazo) {
        this.nombreMeta = nombreMeta;
        this.montoNecesario = montoNecesario;
        this.plazoMeses = plazoMeses;
        this.cuotaMensual = cuotaMensual;
        this.ingresoMensual = ingresoMensual;
        this.presupuestoAhorro20 = presupuestoAhorro20;
        this.presupuestoOcio30 = presupuestoOcio30;
        this.gastoActualOcio30 = gastoActualOcio30;
        this.nuevoPresupuestoOcioSugerido = nuevoPresupuestoOcioSugerido;
        this.esFactible = esFactible;
        this.mensajeDiagnostico = mensajeDiagnostico;
        this.mensajeProteccionNecesidades = mensajeProteccionNecesidades;
        this.recortesSugeridos = recortesSugeridos;
        this.plazoSugeridoViable = plazoSugeridoViable;
        this.cuotaViableSugerida = cuotaViableSugerida;
        this.propuestaExtensionPlazo = propuestaExtensionPlazo;
    }

    public String getNombreMeta() {
        return nombreMeta;
    }

    public void setNombreMeta(String nombreMeta) {
        this.nombreMeta = nombreMeta;
    }

    public BigDecimal getMontoNecesario() {
        return montoNecesario;
    }

    public void setMontoNecesario(BigDecimal montoNecesario) {
        this.montoNecesario = montoNecesario;
    }

    public int getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public BigDecimal getCuotaMensual() {
        return cuotaMensual;
    }

    public void setCuotaMensual(BigDecimal cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public BigDecimal getIngresoMensual() {
        return ingresoMensual;
    }

    public void setIngresoMensual(BigDecimal ingresoMensual) {
        this.ingresoMensual = ingresoMensual;
    }

    public BigDecimal getPresupuestoAhorro20() {
        return presupuestoAhorro20;
    }

    public void setPresupuestoAhorro20(BigDecimal presupuestoAhorro20) {
        this.presupuestoAhorro20 = presupuestoAhorro20;
    }

    public BigDecimal getPresupuestoOcio30() {
        return presupuestoOcio30;
    }

    public void setPresupuestoOcio30(BigDecimal presupuestoOcio30) {
        this.presupuestoOcio30 = presupuestoOcio30;
    }

    public BigDecimal getGastoActualOcio30() {
        return gastoActualOcio30;
    }

    public void setGastoActualOcio30(BigDecimal gastoActualOcio30) {
        this.gastoActualOcio30 = gastoActualOcio30;
    }

    public BigDecimal getNuevoPresupuestoOcioSugerido() {
        return nuevoPresupuestoOcioSugerido;
    }

    public void setNuevoPresupuestoOcioSugerido(BigDecimal nuevoPresupuestoOcioSugerido) {
        this.nuevoPresupuestoOcioSugerido = nuevoPresupuestoOcioSugerido;
    }

    public boolean isEsFactible() {
        return esFactible;
    }

    public void setEsFactible(boolean esFactible) {
        this.esFactible = esFactible;
    }

    public String getMensajeDiagnostico() {
        return mensajeDiagnostico;
    }

    public void setMensajeDiagnostico(String mensajeDiagnostico) {
        this.mensajeDiagnostico = mensajeDiagnostico;
    }

    public String getMensajeProteccionNecesidades() {
        return mensajeProteccionNecesidades;
    }

    public void setMensajeProteccionNecesidades(String mensajeProteccionNecesidades) {
        this.mensajeProteccionNecesidades = mensajeProteccionNecesidades;
    }

    public List<SugerenciaRecorteDto> getRecortesSugeridos() {
        return recortesSugeridos;
    }

    public void setRecortesSugeridos(List<SugerenciaRecorteDto> recortesSugeridos) {
        this.recortesSugeridos = recortesSugeridos;
    }

    public Integer getPlazoSugeridoViable() {
        return plazoSugeridoViable;
    }

    public void setPlazoSugeridoViable(Integer plazoSugeridoViable) {
        this.plazoSugeridoViable = plazoSugeridoViable;
    }

    public BigDecimal getCuotaViableSugerida() {
        return cuotaViableSugerida;
    }

    public void setCuotaViableSugerida(BigDecimal cuotaViableSugerida) {
        this.cuotaViableSugerida = cuotaViableSugerida;
    }

    public String getPropuestaExtensionPlazo() {
        return propuestaExtensionPlazo;
    }

    public void setPropuestaExtensionPlazo(String propuestaExtensionPlazo) {
        this.propuestaExtensionPlazo = propuestaExtensionPlazo;
    }
}