package com.universidad.gestorfinanzas.servicio;

import com.universidad.gestorfinanzas.dto.ActualizarIngresoDto;
import com.universidad.gestorfinanzas.dto.EstadoCategoriaDto;
import com.universidad.gestorfinanzas.dto.EstadoSubcategoriaDto;
import com.universidad.gestorfinanzas.dto.RegistroUsuarioDto;
import com.universidad.gestorfinanzas.dto.RespuestaEnvioCorreoDto;
import com.universidad.gestorfinanzas.dto.RespuestaPlanMetaDto;
import com.universidad.gestorfinanzas.dto.RespuestaTransaccionDto;
import com.universidad.gestorfinanzas.dto.ResumenMensualDto;
import com.universidad.gestorfinanzas.dto.SolicitudAbonoMetaDto;
import com.universidad.gestorfinanzas.dto.SolicitudMetaAhorroDto;
import com.universidad.gestorfinanzas.dto.SolicitudTransaccionDto;
import com.universidad.gestorfinanzas.dto.SugerenciaRecorteDto;
import com.universidad.gestorfinanzas.modelo.Categoria;
import com.universidad.gestorfinanzas.modelo.MetaAhorro;
import com.universidad.gestorfinanzas.modelo.Subcategoria;
import com.universidad.gestorfinanzas.modelo.Transaccion;
import com.universidad.gestorfinanzas.modelo.Usuario;
import com.universidad.gestorfinanzas.repositorio.CategoriaRepositorio;
import com.universidad.gestorfinanzas.repositorio.MetaAhorroRepositorio;
import com.universidad.gestorfinanzas.repositorio.SubcategoriaRepositorio;
import com.universidad.gestorfinanzas.repositorio.TransaccionRepositorio;
import com.universidad.gestorfinanzas.repositorio.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio de logica de negocio para la gestion financiera personal:
 * - Regla presupuestaria 70-30-20 con subcategorias.
 * - Registro y validacion de gastos (picos inusuales, variacion de servicios).
 * - Planificador inteligente de ahorro para metas con optimizacion de ocio.
 */
@Service
public class ServicioFinanzas {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final SubcategoriaRepositorio subcategoriaRepositorio;
    private final TransaccionRepositorio transaccionRepositorio;
    private final MetaAhorroRepositorio metaAhorroRepositorio;
    private final ServicioCorreo servicioCorreo;

    public ServicioFinanzas(UsuarioRepositorio usuarioRepositorio,
                            CategoriaRepositorio categoriaRepositorio,
                            SubcategoriaRepositorio subcategoriaRepositorio,
                            TransaccionRepositorio transaccionRepositorio,
                            MetaAhorroRepositorio metaAhorroRepositorio,
                            ServicioCorreo servicioCorreo) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.categoriaRepositorio = categoriaRepositorio;
        this.subcategoriaRepositorio = subcategoriaRepositorio;
        this.transaccionRepositorio = transaccionRepositorio;
        this.metaAhorroRepositorio = metaAhorroRepositorio;
        this.servicioCorreo = servicioCorreo;
    }

    /**
     * Paso 1: Registra al usuario con su ingreso mensual base.
     */
    public Usuario registrarUsuarioConIngreso(RegistroUsuarioDto dto) {
        if (dto.getIngresoMensual() == null || dto.getIngresoMensual().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El ingreso mensual debe ser mayor a cero.");
        }
        Usuario usuario = new Usuario(
                dto.getNombre() != null ? dto.getNombre().trim() : "Usuario Principal",
                dto.getCorreo() != null ? dto.getCorreo().trim() : "usuario@finanzas.com",
                dto.getIngresoMensual()
        );
        return usuarioRepositorio.save(usuario);
    }

    /**
     * Actualiza el ingreso mensual de un usuario existente.
     */
    public Usuario actualizarIngresoMensual(Long idUsuario, BigDecimal nuevoIngreso) {
        if (nuevoIngreso == null || nuevoIngreso.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El ingreso mensual debe ser mayor a cero.");
        }
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        usuario.setIngresoMensual(nuevoIngreso);
        return usuarioRepositorio.save(usuario);
    }

    public Usuario obtenerUsuario(Long idUsuario) {
        return usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
    }

    /**
     * Lógica especial para 'Servicios Básicos':
     * Consulta si el monto del servicio básico varía respecto al mes anterior si ya fue registrado antes,
     * identificando servicios específicos (Luz, Agua, Gas, Teléfono, Internet) en la descripción.
     */
    public String verificarVariacionServiciosBasicos(Long idUsuario, Long idSubcategoria, BigDecimal montoActual, LocalDate fecha, String descripcion) {
        Subcategoria subcategoria = subcategoriaRepositorio.findById(idSubcategoria).orElse(null);
        if (subcategoria == null) {
            return null;
        }

        String nomSub = subcategoria.getNombre().trim().toLowerCase();
        if (!nomSub.contains("servicio")) {
            return null;
        }

        // Identificar tipo de servicio en la descripción
        String servicioIdentificado = null;
        if (descripcion != null) {
            String desc = descripcion.toLowerCase();
            if (desc.contains("luz") || desc.contains("electricidad") || desc.contains("enel") || desc.contains("electrosur")) {
                servicioIdentificado = "Luz";
            } else if (desc.contains("agua") || desc.contains("sedapal")) {
                servicioIdentificado = "Agua";
            } else if (desc.contains("gas") || desc.contains("calidda") || desc.contains("cálidda")) {
                servicioIdentificado = "Gas";
            } else if (desc.contains("telefono") || desc.contains("teléfono") || desc.contains("celular") || desc.contains("movil") || desc.contains("móvil")) {
                servicioIdentificado = "Teléfono";
            } else if (desc.contains("internet") || desc.contains("wifi") || desc.contains("fibra") || desc.contains("cable")) {
                servicioIdentificado = "Internet";
            }
        }

        // Buscar transacciones históricas en Servicios Básicos para este usuario
        List<Transaccion> historicoServicios = transaccionRepositorio
                .findByUsuarioIdUsuarioAndSubcategoriaIdSubcategoriaAndTipo(idUsuario, idSubcategoria, "GASTO");

        if (historicoServicios == null || historicoServicios.isEmpty()) {
            return "💡 Es tu primer registro en Servicios Básicos: todavía no hay datos anteriores para comparar, ¡este monto será tu punto de partida!";
        }

        BigDecimal montoAnterior = null;
        String etiqueta = servicioIdentificado != null ? ("tu recibo de " + servicioIdentificado) : "Servicios Básicos";

        // Si se identificó servicio específico, buscar el registro anterior más reciente con esa misma palabra clave
        if (servicioIdentificado != null) {
            final String servBusq = servicioIdentificado.toLowerCase();
            for (int i = historicoServicios.size() - 1; i >= 0; i--) {
                Transaccion t = historicoServicios.get(i);
                if (t.getDescripcion() != null && t.getDescripcion().toLowerCase().contains(servBusq)) {
                    montoAnterior = t.getMonto();
                    break;
                }
            }
        }

        // Si no se encontró específico por palabra clave, buscar en el mes anterior
        if (montoAnterior == null) {
            YearMonth mesAnterior = YearMonth.from(fecha).minusMonths(1);
            LocalDate inicioMesAnterior = mesAnterior.atDay(1);
            LocalDate finMesAnterior = mesAnterior.atEndOfMonth();

            List<Transaccion> gastosMesAnterior = transaccionRepositorio
                    .findByUsuarioIdUsuarioAndSubcategoriaNombreIgnoreCaseAndTipoAndFechaBetween(
                            idUsuario, subcategoria.getNombre(), "GASTO", inicioMesAnterior, finMesAnterior);

            if (gastosMesAnterior != null && !gastosMesAnterior.isEmpty()) {
                BigDecimal totalMesAnterior = BigDecimal.ZERO;
                for (Transaccion t : gastosMesAnterior) {
                    totalMesAnterior = totalMesAnterior.add(t.getMonto());
                }
                montoAnterior = totalMesAnterior;
            } else if (!historicoServicios.isEmpty()) {
                // Tomar el gasto anterior más reciente
                montoAnterior = historicoServicios.get(historicoServicios.size() - 1).getMonto();
            }
        }

        if (montoAnterior == null || montoAnterior.compareTo(BigDecimal.ZERO) <= 0) {
            return "💡 Es tu primer registro en Servicios Básicos: todavía no hay datos anteriores para comparar, ¡este monto será tu punto de partida!";
        }

        BigDecimal diferencia = montoActual.subtract(montoAnterior);
        BigDecimal porcentaje = diferencia.abs().multiply(new BigDecimal("100"))
                .divide(montoAnterior, 1, RoundingMode.HALF_UP);

        if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
            return String.format("💡 ¡Ojo con los servicios! Este mes %s (S/. %.2f) subió S/. %.2f (+%.1f%%) comparado con el registro anterior (S/. %.2f). Revisa si hubo algún consumo extra o artefactos encendidos.",
                    etiqueta, montoActual, diferencia, porcentaje, montoAnterior);
        } else if (diferencia.compareTo(BigDecimal.ZERO) < 0) {
            return String.format("💡 ¡Buena noticia! Este mes %s (S/. %.2f) bajó S/. %.2f (-%.1f%%) respecto al registro anterior (S/. %.2f). ¡Buen ahorro en casa!",
                    etiqueta, montoActual, diferencia.abs(), porcentaje, montoAnterior);
        } else {
            return String.format("💡 %s se mantiene estable: gastaste exactamente lo mismo que el registro anterior (S/. %.2f).",
                    etiqueta.substring(0, 1).toUpperCase() + etiqueta.substring(1), montoAnterior);
        }
    }

    public String verificarVariacionServiciosBasicos(Long idUsuario, Long idSubcategoria, BigDecimal montoActual, LocalDate fecha) {
        return verificarVariacionServiciosBasicos(idUsuario, idSubcategoria, montoActual, fecha, null);
    }

    /**
     * Detección de picos inusuales respecto al promedio histórico de la subcategoría.
     */
    public boolean verificarPicoInusual(Long idUsuario, Long idSubcategoria, BigDecimal nuevoMonto) {
        List<Transaccion> gastosHistoricos = transaccionRepositorio
                .findByUsuarioIdUsuarioAndSubcategoriaIdSubcategoriaAndTipo(idUsuario, idSubcategoria, "GASTO");

        if (gastosHistoricos == null || gastosHistoricos.size() < 2) {
            return false;
        }

        BigDecimal sumaMonto = BigDecimal.ZERO;
        for (Transaccion t : gastosHistoricos) {
            sumaMonto = sumaMonto.add(t.getMonto());
        }

        BigDecimal promedioHistorico = sumaMonto.divide(
                BigDecimal.valueOf(gastosHistoricos.size()), 2, RoundingMode.HALF_UP);

        BigDecimal umbralAlerta = promedioHistorico.multiply(new BigDecimal("1.50"));
        return nuevoMonto.compareTo(umbralAlerta) > 0;
    }

    /**
     * Verifica si se supera el presupuesto asignado de la categoría padre (regla 70-30-20).
     */
    public boolean verificarSuperaPorcentajeAsignado(Usuario usuario, Categoria categoria, int mes, int anio, BigDecimal nuevoMonto) {
        YearMonth periodo = YearMonth.of(anio, mes);
        LocalDate fechaInicio = periodo.atDay(1);
        LocalDate fechaFin = periodo.atEndOfMonth();

        List<Transaccion> transaccionesMes = transaccionRepositorio
                .findByUsuarioIdUsuarioAndFechaBetween(usuario.getIdUsuario(), fechaInicio, fechaFin);

        BigDecimal gastoAcumulado = BigDecimal.ZERO;
        for (Transaccion t : transaccionesMes) {
            if ("GASTO".equalsIgnoreCase(t.getTipo()) && 
                t.getSubcategoria().getCategoria().getIdCategoria().equals(categoria.getIdCategoria())) {
                gastoAcumulado = gastoAcumulado.add(t.getMonto());
            }
        }

        if (nuevoMonto != null) {
            gastoAcumulado = gastoAcumulado.add(nuevoMonto);
        }

        BigDecimal limitePermitido = usuario.getIngresoMensual()
                .multiply(categoria.getPorcentajeMeta())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        return gastoAcumulado.compareTo(limitePermitido) > 0;
    }

    /**
     * Registra un gasto o ingreso asignado a una subcategoría con validaciones integradas.
     */
    public RespuestaTransaccionDto registrarTransaccion(SolicitudTransaccionDto solicitud) {
        Usuario usuario = usuarioRepositorio.findById(solicitud.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + solicitud.getIdUsuario()));

        Subcategoria subcategoria = subcategoriaRepositorio.findById(solicitud.getIdSubcategoria())
                .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada con ID: " + solicitud.getIdSubcategoria()));

        Categoria categoria = subcategoria.getCategoria();
        LocalDate fecha = (solicitud.getFecha() != null) ? solicitud.getFecha() : LocalDate.now();
        String tipo = (solicitud.getTipo() != null) ? solicitud.getTipo().toUpperCase() : "GASTO";

        boolean esPico = false;
        String mensajePico = null;
        boolean superaPresupuesto = false;
        String mensajePresupuesto = null;
        boolean variacionServicio = false;
        String mensajeVariacion = null;

        if ("GASTO".equals(tipo)) {
            // 1. Variación de Servicios Básicos
            mensajeVariacion = verificarVariacionServiciosBasicos(usuario.getIdUsuario(), subcategoria.getIdSubcategoria(), solicitud.getMonto(), fecha, solicitud.getDescripcion());
            variacionServicio = (mensajeVariacion != null);

            // 2. Detección de pico inusual
            esPico = verificarPicoInusual(usuario.getIdUsuario(), subcategoria.getIdSubcategoria(), solicitud.getMonto());
            if (esPico) {
                mensajePico = String.format("⚡ ¡Cuidado! Este gasto de S/. %.2f está más de 50%% por encima de lo que sueles gastar en '%s'. ¿Fue una emergencia o una compra imprevista?",
                        solicitud.getMonto(), subcategoria.getNombre());
            }

            // 3. Verificación de regla 70-30-20
            superaPresupuesto = verificarSuperaPorcentajeAsignado(
                    usuario, categoria, fecha.getMonthValue(), fecha.getYear(), solicitud.getMonto());
            if (superaPresupuesto) {
                BigDecimal limite = usuario.getIngresoMensual()
                        .multiply(categoria.getPorcentajeMeta())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                mensajePresupuesto = String.format("⚠️ ¡Te pasaste del presupuesto! Acabas de superar el tope asignado para '%s' (límite: %.0f%% = S/. %.2f). Toca frenar un poco en este bloque.",
                        categoria.getNombre(), categoria.getPorcentajeMeta(), limite);
            }
        }

        Transaccion transaccion = new Transaccion(
                solicitud.getMonto(),
                fecha,
                tipo,
                solicitud.getDescripcion(),
                subcategoria,
                usuario
        );
        Transaccion guardada = transaccionRepositorio.save(transaccion);

        RespuestaTransaccionDto respuesta = new RespuestaTransaccionDto(
                guardada, esPico, mensajePico, superaPresupuesto, mensajePresupuesto, variacionServicio, mensajeVariacion);

        // Verificación de riesgo y desviación sobre meta activa
        if ("GASTO".equals(tipo)) {
            verificarDesviacionMeta(usuario, subcategoria, solicitud.getMonto(), fecha, respuesta);
        }

        return respuesta;
    }

    /**
     * Resumen financiero mensual jerárquico (Categorías principales y subcategorías).
     */
    public ResumenMensualDto obtenerResumenMensual(Long idUsuario, int mes, int anio) {
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        YearMonth periodo = YearMonth.of(anio, mes);
        LocalDate fechaInicio = periodo.atDay(1);
        LocalDate fechaFin = periodo.atEndOfMonth();

        List<Transaccion> transaccionesMes = transaccionRepositorio
                .findByUsuarioIdUsuarioAndFechaBetween(idUsuario, fechaInicio, fechaFin);
        List<Categoria> todasCategorias = categoriaRepositorio.findAll();

        BigDecimal totalGastado = BigDecimal.ZERO;
        List<EstadoCategoriaDto> listaEstados = new ArrayList<>();

        for (Categoria cat : todasCategorias) {
            BigDecimal gastoCategoria = BigDecimal.ZERO;
            List<EstadoSubcategoriaDto> listaSubestados = new ArrayList<>();

            List<Subcategoria> subcats = subcategoriaRepositorio.findByCategoriaIdCategoria(cat.getIdCategoria());
            for (Subcategoria sub : subcats) {
                BigDecimal gastoSub = BigDecimal.ZERO;
                for (Transaccion t : transaccionesMes) {
                    if ("GASTO".equalsIgnoreCase(t.getTipo()) && 
                        t.getSubcategoria().getIdSubcategoria().equals(sub.getIdSubcategoria())) {
                        gastoSub = gastoSub.add(t.getMonto());
                    }
                }
                gastoCategoria = gastoCategoria.add(gastoSub);
                listaSubestados.add(new EstadoSubcategoriaDto(sub.getIdSubcategoria(), sub.getNombre(), gastoSub));
            }

            totalGastado = totalGastado.add(gastoCategoria);

            BigDecimal limitePermitido = usuario.getIngresoMensual()
                    .multiply(cat.getPorcentajeMeta())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            BigDecimal saldoRestante = limitePermitido.subtract(gastoCategoria);
            boolean supera = gastoCategoria.compareTo(limitePermitido) > 0;

            BigDecimal porcentajeConsumido = BigDecimal.ZERO;
            if (limitePermitido.compareTo(BigDecimal.ZERO) > 0) {
                porcentajeConsumido = gastoCategoria.multiply(new BigDecimal("100"))
                        .divide(limitePermitido, 2, RoundingMode.HALF_UP);
            }

            listaEstados.add(new EstadoCategoriaDto(
                    cat.getIdCategoria(),
                    cat.getNombre(),
                    cat.getPorcentajeMeta(),
                    limitePermitido,
                    gastoCategoria,
                    saldoRestante,
                    porcentajeConsumido,
                    supera,
                    listaSubestados
            ));
        }

        BigDecimal saldoDisponible = usuario.getIngresoMensual().subtract(totalGastado);

        return new ResumenMensualDto(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                mes,
                anio,
                usuario.getIngresoMensual(),
                totalGastado,
                saldoDisponible,
                listaEstados
        );
    }

    /**
     * Lógica Especial 'Ahorro para Metas':
     * Calcula la cuota mensual (plazo de 1 a 12 meses) y optimiza el presupuesto
     * sugiriendo recortes EXCLUSIVAMENTE en el bloque del 30% (Ocio y gustos),
     * protegiendo al 100% las necesidades básicas del 70%.
     */
    public RespuestaPlanMetaDto evaluarPlanMetaAhorro(SolicitudMetaAhorroDto solicitud) {
        if (solicitud.getPlazoMeses() < 1 || solicitud.getPlazoMeses() > 12) {
            throw new IllegalArgumentException("El plazo en meses debe estar comprendido entre 1 y 12 meses.");
        }
        if (solicitud.getMontoNecesario() == null || solicitud.getMontoNecesario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto necesario para la meta debe ser mayor a cero.");
        }

        Usuario usuario = usuarioRepositorio.findById(solicitud.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + solicitud.getIdUsuario()));

        BigDecimal ingreso = usuario.getIngresoMensual();

        // 1. Cálculo de cuota mensual para el plazo solicitado
        BigDecimal cuotaMensual = solicitud.getMontoNecesario().divide(
                BigDecimal.valueOf(solicitud.getPlazoMeses()), 2, RoundingMode.HALF_UP);

        // 2. Bloques presupuestarios de la regla 70-30-20
        BigDecimal limiteNecesidades70 = ingreso.multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal limiteOcio30 = ingreso.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal limiteAhorro20 = ingreso.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);

        // 3. Gastos actuales reales del usuario en el mes en curso por subcategoría de Ocio
        LocalDate hoy = LocalDate.now();
        YearMonth periodo = YearMonth.of(hoy.getYear(), hoy.getMonthValue());
        List<Transaccion> transaccionesMes = transaccionRepositorio.findByUsuarioIdUsuarioAndFechaBetween(
                usuario.getIdUsuario(), periodo.atDay(1), periodo.atEndOfMonth());

        Map<String, BigDecimal> gastosPorSubcategoria = new HashMap<>();
        gastosPorSubcategoria.put("Entretenimiento", BigDecimal.ZERO);
        gastosPorSubcategoria.put("Compras personales", BigDecimal.ZERO);
        gastosPorSubcategoria.put("Suscripciones de entretenimiento", BigDecimal.ZERO);
        gastosPorSubcategoria.put("Viajes de placer", BigDecimal.ZERO);

        BigDecimal gastoActualOcioTotal = BigDecimal.ZERO;
        for (Transaccion t : transaccionesMes) {
            if ("GASTO".equalsIgnoreCase(t.getTipo()) &&
                t.getSubcategoria() != null &&
                t.getSubcategoria().getCategoria() != null &&
                "Ocio y gustos".equalsIgnoreCase(t.getSubcategoria().getCategoria().getNombre().trim())) {
                String sub = t.getSubcategoria().getNombre().trim();
                BigDecimal m = t.getMonto();
                gastoActualOcioTotal = gastoActualOcioTotal.add(m);
                String subKey = "Entretenimiento";
                String subLower = sub.toLowerCase();
                if (subLower.contains("compra")) {
                    subKey = "Compras personales";
                } else if (subLower.contains("suscrip")) {
                    subKey = "Suscripciones de entretenimiento";
                } else if (subLower.contains("viaje")) {
                    subKey = "Viajes de placer";
                } else if (subLower.contains("entreten")) {
                    subKey = "Entretenimiento";
                }
                gastosPorSubcategoria.put(subKey, gastosPorSubcategoria.getOrDefault(subKey, BigDecimal.ZERO).add(m));
            }
        }

        // 4. Evaluación de factibilidad matemática y financiera:
        // Una cuota es factible si no supera el margen saludable de ocio (máx 60% del bloque)
        // y jamás es viable si la cuota supera el bloque entero de ocio (ej. S/. 800 vs S/. 600).
        BigDecimal maxRecorteOcioViable = limiteOcio30.multiply(new BigDecimal("0.60")).setScale(2, RoundingMode.HALF_UP);
        boolean esFactible = cuotaMensual.compareTo(maxRecorteOcioViable) <= 0;

        // 5. Cálculo del plazo sugerido si la cuota no es viable
        Integer plazoSugeridoViable = null;
        BigDecimal cuotaViableSugerida = null;
        String propuestaExtension = null;

        if (!esFactible) {
            // Cuota viable objetivo: 50% del bloque de ocio (o al menos S/. 50.00)
            BigDecimal cuotaObjetivo = limiteOcio30.multiply(new BigDecimal("0.50")).max(new BigDecimal("50.00"));
            int mesesCalculados = (int) Math.ceil(solicitud.getMontoNecesario().doubleValue() / cuotaObjetivo.doubleValue());
            plazoSugeridoViable = Math.max(mesesCalculados, solicitud.getPlazoMeses() + 1);
            cuotaViableSugerida = solicitud.getMontoNecesario().divide(BigDecimal.valueOf(plazoSugeridoViable), 2, RoundingMode.HALF_UP);

            if (cuotaMensual.compareTo(limiteOcio30) >= 0) {
                propuestaExtension = String.format(
                    "Oye, esta cuota de S/. %.2f al mes supera todo tu bloque de ocio (S/. %.2f). Para no asfixiarte ni tocar tus gastos del día a día, te conviene estirar el plazo a %d meses (la cuota baja a S/. %.2f/mes, ¡mucho más manejable!).",
                    cuotaMensual, limiteOcio30, plazoSugeridoViable, cuotaViableSugerida
                );
            } else {
                propuestaExtension = String.format(
                    "Tu cuota de S/. %.2f/mes se come casi todo tu ocio (S/. %.2f). Te recomendamos ampliar el plazo a %d meses para que la cuota baje a S/. %.2f/mes y sigas disfrutando de tus gustitos con tranquilidad.",
                    cuotaMensual, limiteOcio30, plazoSugeridoViable, cuotaViableSugerida
                );
            }
        }

        // 6. Generación de las 4 subcategorías con la estructura exacta de 5 columnas:
        // 1. Subcategoría Ocio (30%)
        // 2. Límite Base
        // 3. Gasto Actual (lo que ya se consumió en el mes)
        // 4. Nuevo Límite (estricto y coherente: saldo restante real, NUNCA igual al gasto actual si se agotó el saldo)
        // 5. Consejo / Diagnóstico financiero
        List<SugerenciaRecorteDto> recortes = new ArrayList<>();
        String[] nombresSub = {"Entretenimiento", "Compras personales", "Suscripciones de entretenimiento", "Viajes de placer"};
        BigDecimal[] pesos = {new BigDecimal("0.35"), new BigDecimal("0.25"), new BigDecimal("0.25"), new BigDecimal("0.15")};
        String[] consejosBase = {
            "Modera salidas a restaurantes, bares, cine, conciertos y reuniones con amigos.",
            "Frena compras impulsivas de ropa, calzado, tecnología y accesorios.",
            "Revisa y pausa temporalmente plataformas de streaming que casi no uses.",
            "Posterga vacaciones y viajes de placer no programados."
        };

        BigDecimal sumaNuevosLimites = BigDecimal.ZERO;

        for (int i = 0; i < nombresSub.length; i++) {
            String sub = nombresSub[i];
            BigDecimal peso = pesos[i];
            BigDecimal limiteBase = limiteOcio30.multiply(peso).setScale(2, RoundingMode.HALF_UP);
            BigDecimal gastoActual = gastosPorSubcategoria.getOrDefault(sub, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            BigDecimal nuevoLimite;
            String consejo;

            if (esFactible) {
                BigDecimal recorteSub = cuotaMensual.multiply(peso).setScale(2, RoundingMode.HALF_UP);
                BigDecimal presupuestoAjustado = limiteBase.subtract(recorteSub);
                if (presupuestoAjustado.compareTo(BigDecimal.ZERO) < 0) {
                    presupuestoAjustado = BigDecimal.ZERO;
                }

                // Si ya gastó todo el margen permitido o más:
                if (gastoActual.compareTo(presupuestoAjustado) >= 0) {
                    nuevoLimite = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                    consejo = String.format("Ya registraste S/. %.2f aquí este mes y agotaste el margen de esta meta. Tu tope para lo que queda del mes es S/. 0.00 (¡cero gastos adicionales aquí!).", gastoActual);
                } else {
                    // Queda saldo restante real
                    nuevoLimite = presupuestoAjustado.subtract(gastoActual).setScale(2, RoundingMode.HALF_UP);
                    if (gastoActual.compareTo(BigDecimal.ZERO) == 0) {
                        consejo = String.format("%s Tienes hasta S/. %.2f libres para lo que resta del mes.", consejosBase[i], nuevoLimite);
                    } else {
                        consejo = String.format("%s Llevas S/. %.2f consumidos y aún te quedan S/. %.2f disponibles este mes.", consejosBase[i], gastoActual, nuevoLimite);
                    }
                }
            } else {
                // Caso no factible con el plazo actual (ej: cuota S/. 800 vs ocio S/. 600):
                if (cuotaMensual.compareTo(limiteOcio30) >= 0) {
                    nuevoLimite = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                    if (gastoActual.compareTo(BigDecimal.ZERO) > 0) {
                        consejo = String.format("Con esta cuota tan alta y habiendo gastado ya S/. %.2f, no te queda margen en %s (tope restante: S/. 0.00). Conviene estirar el plazo a %d meses.",
                                gastoActual, sub, plazoSugeridoViable);
                    } else {
                        consejo = String.format("La cuota absorbe todo este presupuesto (tope restante: S/. 0.00). Pasa tu meta a %d meses para recuperar margen.", plazoSugeridoViable);
                    }
                } else {
                    BigDecimal recorteSub = cuotaMensual.multiply(peso).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal presupuestoAjustado = limiteBase.subtract(recorteSub);

                    if (presupuestoAjustado.compareTo(BigDecimal.ZERO) <= 0 || gastoActual.compareTo(presupuestoAjustado) >= 0) {
                        nuevoLimite = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                        consejo = String.format("Ya gastaste S/. %.2f este mes. Con esta cuota tan apretada no te queda saldo (S/. 0.00). ¡Pásate a %d meses para respirar!", gastoActual, plazoSugeridoViable);
                    } else {
                        nuevoLimite = presupuestoAjustado.subtract(gastoActual).setScale(2, RoundingMode.HALF_UP);
                        consejo = String.format("Apenas te restan S/. %.2f este mes. Es un recorte muy forzado; te sugerimos ampliar el plazo a %d meses.", nuevoLimite, plazoSugeridoViable);
                    }
                }
            }

            sumaNuevosLimites = sumaNuevosLimites.add(nuevoLimite);
            // Orden exacto de constructor: (nombreSubcategoria, limiteBase, gastoActual, nuevoLimite, consejo)
            recortes.add(new SugerenciaRecorteDto(sub, limiteBase, gastoActual, nuevoLimite, consejo));
        }

        BigDecimal nuevoPresupuestoOcio = sumaNuevosLimites;

        String mensajeDiagnostico;
        if (esFactible) {
            mensajeDiagnostico = String.format(
                    "¡Meta totalmente viable! Para juntar tus S/. %.2f de '%s' en %d meses necesitas una cuota de S/. %.2f al mes. " +
                    "Apretando un poco tus gustitos y salidas (tu nuevo saldo total de ocio para el mes será S/. %.2f), ¡llegas sobrado a tu meta!",
                    solicitud.getMontoNecesario(), solicitud.getNombreMeta(), solicitud.getPlazoMeses(),
                    cuotaMensual, nuevoPresupuestoOcio
            );
        } else {
            mensajeDiagnostico = String.format(
                    "Oye, esta meta está muy brava para este plazo: juntar S/. %.2f en %d meses te pide S/. %.2f al mes, " +
                    "pero todo tu bloque de ocio es de solo S/. %.2f. Sería imposible cumplirla sin endeudarte o sacrificar necesidades básicas. " +
                    "Te sugerimos ampliar el plazo a %d meses (cuota amigable: S/. %.2f/mes) para ahorrar con tranquilidad y sin asfixiarte.",
                    solicitud.getMontoNecesario(), solicitud.getPlazoMeses(), cuotaMensual,
                    limiteOcio30, plazoSugeridoViable, cuotaViableSugerida
            );
        }

        String mensajeProteccion = String.format(
                "🛡️ ¡Tranqui! Tu 70%% de necesidades básicas (S/. %.2f) está 100%% blindado: vivienda, comida y servicios no se tocan.",
                limiteNecesidades70
        );

        return new RespuestaPlanMetaDto(
                solicitud.getNombreMeta(),
                solicitud.getMontoNecesario(),
                solicitud.getPlazoMeses(),
                cuotaMensual,
                ingreso,
                limiteAhorro20,
                limiteOcio30,
                gastoActualOcioTotal,
                nuevoPresupuestoOcio,
                esFactible,
                mensajeDiagnostico,
                mensajeProteccion,
                recortes,
                plazoSugeridoViable,
                cuotaViableSugerida,
                propuestaExtension
        );
    }

    /**
     * Guarda la meta de ahorro en la base de datos si el usuario decide confirmarla.
     */
    public MetaAhorro guardarMetaAhorro(SolicitudMetaAhorroDto solicitud) {
        Usuario usuario = usuarioRepositorio.findById(solicitud.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + solicitud.getIdUsuario()));

        BigDecimal cuotaMensual = solicitud.getMontoNecesario().divide(
                BigDecimal.valueOf(solicitud.getPlazoMeses()), 2, RoundingMode.HALF_UP);

        MetaAhorro meta = new MetaAhorro(
                solicitud.getNombreMeta(),
                solicitud.getMontoNecesario(),
                solicitud.getPlazoMeses(),
                cuotaMensual,
                LocalDate.now(),
                usuario
        );
        return metaAhorroRepositorio.save(meta);
    }

    /**
     * Módulo de Depósitos y Progreso: Registra un abono/aporte a una meta de ahorro activa.
     */
    public MetaAhorro registrarAbonoMeta(Long idMeta, SolicitudAbonoMetaDto dto) {
        if (dto.getMontoAbono() == null || dto.getMontoAbono().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a abonar debe ser mayor a cero.");
        }
        MetaAhorro meta = metaAhorroRepositorio.findById(idMeta)
                .orElseThrow(() -> new RuntimeException("Meta de ahorro no encontrada con ID: " + idMeta));

        meta.abonar(dto.getMontoAbono());
        MetaAhorro metaGuardada = metaAhorroRepositorio.save(meta);

        // Registrar transacción de ahorro bajo el bloque de 20% Ahorro e Inversión
        try {
            List<Categoria> categorias = categoriaRepositorio.findAll();
            for (Categoria c : categorias) {
                if (c.getNombre() != null && c.getNombre().toLowerCase().contains("ahorro")) {
                    List<Subcategoria> subcats = subcategoriaRepositorio.findByCategoriaIdCategoria(c.getIdCategoria());
                    if (!subcats.isEmpty()) {
                        Subcategoria subcatAhorro = subcats.get(0);
                        String descripcion = (dto.getDescripcion() != null && !dto.getDescripcion().isBlank())
                                ? dto.getDescripcion().trim()
                                : "Aporte a meta: " + meta.getNombreMeta();
                        Transaccion transaccion = new Transaccion(
                                dto.getMontoAbono(),
                                dto.getFecha() != null ? dto.getFecha() : LocalDate.now(),
                                "GASTO",
                                descripcion,
                                subcatAhorro,
                                meta.getUsuario()
                        );
                        transaccionRepositorio.save(transaccion);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // Ignorar para no bloquear el abono a la meta
        }

        return metaGuardada;
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepositorio.findAll();
    }

    public List<Subcategoria> listarSubcategoriasPorCategoria(Long idCategoria) {
        return subcategoriaRepositorio.findByCategoriaIdCategoria(idCategoria);
    }

    public List<Transaccion> listarTransaccionesUsuario(Long idUsuario) {
        return transaccionRepositorio.findByUsuarioIdUsuarioOrderByFechaDesc(idUsuario);
    }

    public List<MetaAhorro> listarMetasUsuario(Long idUsuario) {
        return metaAhorroRepositorio.findByUsuarioIdUsuarioOrderByFechaCreacionDesc(idUsuario);
    }

    /**
     * Alerta por Desviación de Meta:
     * Si el usuario registra un gasto en 'Ocio y gustos' y cuenta con una meta activa,
     * se verifica si el gasto acumulado en ocio del mes supera el margen de ocio optimizado.
     */
    public void verificarDesviacionMeta(Usuario usuario, Subcategoria subcategoria, BigDecimal nuevoMonto, LocalDate fecha, RespuestaTransaccionDto respuesta) {
        if (subcategoria.getCategoria() == null || !"Ocio y gustos".equalsIgnoreCase(subcategoria.getCategoria().getNombre().trim())) {
            return;
        }

        Optional<MetaAhorro> metaOpt = metaAhorroRepositorio.findFirstByUsuarioIdUsuarioOrderByFechaCreacionDesc(usuario.getIdUsuario());
        if (metaOpt.isEmpty()) {
            return;
        }

        MetaAhorro meta = metaOpt.get();

        // Presupuesto base del bloque Ocio: 30% del ingreso mensual
        BigDecimal limiteOcio30 = usuario.getIngresoMensual()
                .multiply(new BigDecimal("0.30"))
                .setScale(2, RoundingMode.HALF_UP);

        // Margen optimizado de ocio para poder cumplir la cuota de ahorro
        BigDecimal margenOcioOptimizado = limiteOcio30.subtract(meta.getCuotaMensual());
        if (margenOcioOptimizado.compareTo(BigDecimal.ZERO) < 0) {
            margenOcioOptimizado = BigDecimal.ZERO;
        }

        // Consultar el total gastado en el mes en el bloque de Ocio
        YearMonth periodo = YearMonth.of(fecha.getYear(), fecha.getMonthValue());
        List<Transaccion> transaccionesMes = transaccionRepositorio.findByUsuarioIdUsuarioAndFechaBetween(
                usuario.getIdUsuario(), periodo.atDay(1), periodo.atEndOfMonth());

        BigDecimal gastoAcumuladoOcio = BigDecimal.ZERO;
        for (Transaccion t : transaccionesMes) {
            if ("GASTO".equalsIgnoreCase(t.getTipo()) &&
                t.getSubcategoria() != null &&
                t.getSubcategoria().getCategoria() != null &&
                "Ocio y gustos".equalsIgnoreCase(t.getSubcategoria().getCategoria().getNombre().trim())) {
                gastoAcumuladoOcio = gastoAcumuladoOcio.add(t.getMonto());
            }
        }

        // Si el total acumulado en ocio sobrepasa el margen optimizado
        if (gastoAcumuladoOcio.compareTo(margenOcioOptimizado) > 0) {
            BigDecimal exceso = gastoAcumuladoOcio.subtract(margenOcioOptimizado);
            respuesta.setAlertaDesviacionMeta(true);
            respuesta.setIdMetaAfectada(meta.getIdMeta());
            respuesta.setNombreMetaAfectada(meta.getNombreMeta());
            respuesta.setMensajeDesviacionMeta(String.format(
                    "🚨 ¡Ojo aquí! Con este gasto de S/. %.2f en ocio ya llevas S/. %.2f acumulados en el mes, pasando tu tope optimizado de S/. %.2f (te pasaste por S/. %.2f). Si sigues gastando en gustitos o salidas, tu meta '%s' (cuota: S/. %.2f/mes) va a peligrar. ¡Te recomendamos moderar gastos o replanificar tu plazo para respirar más tranquilo!",
                    nuevoMonto, gastoAcumuladoOcio, margenOcioOptimizado, exceso, meta.getNombreMeta(), meta.getCuotaMensual()
            ));
        }
    }

    /**
     * Genera y despacha el reporte formal de la meta de ahorro al correo registrado del usuario.
     */
    public RespuestaEnvioCorreoDto enviarReporteMetaPorCorreo(Long idUsuario, Long idMeta) {
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        MetaAhorro meta = metaAhorroRepositorio.findById(idMeta)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada con ID: " + idMeta));

        SolicitudMetaAhorroDto solicitud = new SolicitudMetaAhorroDto(
                idUsuario, meta.getNombreMeta(), meta.getMontoNecesario(), meta.getPlazoMeses());
        RespuestaPlanMetaDto plan = evaluarPlanMetaAhorro(solicitud);

        return servicioCorreo.enviarReporteMeta(usuario, meta, plan);
    }
}