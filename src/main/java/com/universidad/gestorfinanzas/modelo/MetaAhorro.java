package com.universidad.gestorfinanzas.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa una meta de ahorro planificada por el usuario.
 */
@Entity
@Table(name = "metas_ahorro")
public class MetaAhorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meta")
    private Long idMeta;

    @Column(name = "nombre_meta", nullable = false, length = 100)
    private String nombreMeta;

    @Column(name = "monto_necesario", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoNecesario;

    @Column(name = "plazo_meses", nullable = false)
    private int plazoMeses;

    @Column(name = "cuota_mensual", nullable = false, precision = 12, scale = 2)
    private BigDecimal cuotaMensual;

    @Column(name = "monto_acumulado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoAcumulado = BigDecimal.ZERO;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    public MetaAhorro() {
    }

    public MetaAhorro(String nombreMeta, BigDecimal montoNecesario, int plazoMeses, BigDecimal cuotaMensual, LocalDate fechaCreacion, Usuario usuario) {
        this.nombreMeta = nombreMeta;
        this.montoNecesario = montoNecesario;
        this.plazoMeses = plazoMeses;
        this.cuotaMensual = cuotaMensual;
        this.montoAcumulado = BigDecimal.ZERO;
        this.fechaCreacion = fechaCreacion;
        this.usuario = usuario;
    }

    public Long getIdMeta() {
        return idMeta;
    }

    public void setIdMeta(Long idMeta) {
        this.idMeta = idMeta;
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

    public BigDecimal getMontoAcumulado() {
        return montoAcumulado != null ? montoAcumulado : BigDecimal.ZERO;
    }

    public void setMontoAcumulado(BigDecimal montoAcumulado) {
        this.montoAcumulado = montoAcumulado != null ? montoAcumulado : BigDecimal.ZERO;
    }

    public void abonar(BigDecimal monto) {
        if (monto != null && monto.compareTo(BigDecimal.ZERO) > 0) {
            this.montoAcumulado = getMontoAcumulado().add(monto);
        }
    }

    public BigDecimal getSaldoPendiente() {
        if (montoNecesario == null) return BigDecimal.ZERO;
        BigDecimal pendiente = montoNecesario.subtract(getMontoAcumulado());
        return pendiente.compareTo(BigDecimal.ZERO) > 0 ? pendiente : BigDecimal.ZERO;
    }

    public BigDecimal getPorcentajeAvance() {
        if (montoNecesario == null || montoNecesario.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return getMontoAcumulado().multiply(new BigDecimal("100"))
                .divide(montoNecesario, 1, java.math.RoundingMode.HALF_UP);
    }

    public boolean isCompletada() {
        return montoNecesario != null && getMontoAcumulado().compareTo(montoNecesario) >= 0;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}