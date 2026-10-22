package com.universidad.gestorfinanzas.servicio;

import com.universidad.gestorfinanzas.dto.RespuestaEnvioCorreoDto;
import com.universidad.gestorfinanzas.dto.RespuestaPlanMetaDto;
import com.universidad.gestorfinanzas.dto.SugerenciaRecorteDto;
import com.universidad.gestorfinanzas.modelo.MetaAhorro;
import com.universidad.gestorfinanzas.modelo.Usuario;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio encargado de la generación y despacho manual del reporte de metas de ahorro al correo del usuario.
 * Diseñado con estilo limpio para proyectos académicos universitarios.
 */
@Service
public class ServicioCorreo {

    /**
     * Construye y despacha el reporte formal de planificación y optimización financiera.
     */
    public RespuestaEnvioCorreoDto enviarReporteMeta(Usuario usuario, MetaAhorro meta, RespuestaPlanMetaDto plan) {
        if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario no tiene una dirección de correo electrónico registrada.");
        }

        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String asunto = "🎯 ¡Tu Plan de Ahorro para '" + meta.getNombreMeta() + "' está listo! (Regla 70-30-20)";

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("         GESTOR DE FINANZAS PERSONALES | TU HOJA DE RUTA FINANCIERA    \n");
        sb.append("========================================================================\n");
        sb.append("Fecha:         ").append(fechaHora).append("\n");
        sb.append("Para:          ").append(usuario.getNombre()).append(" <").append(usuario.getCorreo()).append(">\n");
        sb.append("Tu Ingreso:    S/. ").append(String.format("%.2f", usuario.getIngresoMensual())).append(" al mes\n\n");

        sb.append("------------------------------------------------------------------------\n");
        sb.append("1. TU META DE AHORRO\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append(" • Meta:            ").append(meta.getNombreMeta()).append("\n");
        sb.append(" • Monto objetivo:  S/. ").append(String.format("%.2f", meta.getMontoNecesario())).append("\n");
        sb.append(" • Plazo:           ").append(meta.getPlazoMeses()).append(" meses\n");
        sb.append(" • Tu cuota al mes: S/. ").append(String.format("%.2f", meta.getCuotaMensual())).append("\n\n");

        sb.append("------------------------------------------------------------------------\n");
        sb.append("2. TUS GASTOS BÁSICOS ESTÁN BLINDADOS (70%)\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append(" ").append(plan.getMensajeProteccionNecesidades()).append("\n");
        sb.append(" Tus gastos vitales (vivienda, comida, luz, agua, salud y estudios)\n");
        sb.append(" no se tocan para nada.\n\n");

        sb.append("------------------------------------------------------------------------\n");
        sb.append("3. OPTIMIZACIÓN EXCLUSIVA EN OCIO Y GUSTITOS (30%)\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append(" • Tu presupuesto base de ocio:  S/. ").append(String.format("%.2f", plan.getPresupuestoOcio30())).append("\n");
        sb.append(" • Cuota que necesitas para la meta: S/. ").append(String.format("%.2f", plan.getCuotaMensual())).append("\n");
        sb.append(" • Tu nuevo saldo de ocio al mes:    S/. ").append(String.format("%.2f", plan.getNuevoPresupuestoOcioSugerido())).append("\n\n");
        sb.append(" Tabla de Optimización Financiera (5 Columnas):\n");
        sb.append(" Subcategoría Ocio (30%)  | Límite Base | Gasto Actual | Nuevo Límite | Consejo / Diagnóstico\n");
        sb.append(" -----------------------------------------------------------------------------------------\n");

        if (plan.getRecortesSugeridos() != null) {
            for (SugerenciaRecorteDto r : plan.getRecortesSugeridos()) {
                sb.append(String.format(" • %-22s | S/. %7.2f | S/. %7.2f | S/. %7.2f\n",
                        r.getNombreSubcategoria(), r.getLimiteBase(), r.getGastoActual(), r.getNuevoLimite()));
                sb.append("   Consejo / Diagnóstico: ").append(r.getConsejo()).append("\n");
            }
        }

        if (plan.getPropuestaExtensionPlazo() != null) {
            sb.append("\n------------------------------------------------------------------------\n");
            sb.append("4. CONSEJO: ¿CONVIENE ESTIRAR EL PLAZO?\n");
            sb.append("------------------------------------------------------------------------\n");
            sb.append(" ").append(plan.getPropuestaExtensionPlazo()).append("\n");
        }

        sb.append("\n------------------------------------------------------------------------\n");
        sb.append("5. DIAGNÓSTICO FINANCIERO\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append(" ").append(plan.getMensajeDiagnostico()).append("\n\n");
        sb.append("========================================================================\n");
        sb.append("        ¡A darle con todo para alcanzar esa meta! | Gestor de Finanzas  \n");
        sb.append("========================================================================\n");

        String cuerpoReporte = sb.toString();

        // En un entorno académico de desarrollo se simula y audita en consola la emisión del correo
        System.out.println("\n[SISTEMA DE NOTIFICACIONES] ========================================");
        System.out.println("Enviando correo a: " + usuario.getCorreo());
        System.out.println("Asunto: " + asunto);
        System.out.println(cuerpoReporte);
        System.out.println("[FIN DE ENVÍO DE CORREO] ===========================================\n");

        String mensajeUsuario = "¡Todo listo! Ya te mandamos tu reporte completo a tu correo: " + usuario.getCorreo();

        return new RespuestaEnvioCorreoDto(true, usuario.getCorreo(), asunto, mensajeUsuario, cuerpoReporte);
    }
}
