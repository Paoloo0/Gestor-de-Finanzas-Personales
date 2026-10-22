package com.universidad.gestorfinanzas.servicio;

import com.universidad.gestorfinanzas.dto.*;
import com.universidad.gestorfinanzas.modelo.*;
import com.universidad.gestorfinanzas.repositorio.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de la lógica matemática y de negocio de ServicioFinanzas
 * usando Dynamic Proxies estándar de Java.
 */
class ServicioFinanzasTest {

    @SuppressWarnings("unchecked")
    private UsuarioRepositorio crearProxyUsuario(Usuario usuario) {
        return (UsuarioRepositorio) Proxy.newProxyInstance(
                UsuarioRepositorio.class.getClassLoader(),
                new Class<?>[]{UsuarioRepositorio.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        return Optional.ofNullable(usuario);
                    }
                    return null;
                }
        );
    }

    @SuppressWarnings("unchecked")
    private TransaccionRepositorio crearProxyTransaccion(List<Transaccion> transacciones) {
        return (TransaccionRepositorio) Proxy.newProxyInstance(
                TransaccionRepositorio.class.getClassLoader(),
                new Class<?>[]{TransaccionRepositorio.class},
                (proxy, method, args) -> {
                    if ("findByUsuarioIdUsuarioAndFechaBetween".equals(method.getName())) {
                        return transacciones;
                    }
                    return Collections.emptyList();
                }
        );
    }

    @Test
    @DisplayName("Debe sugerir extender plazo y fijar nuevoLimite en 0.00 si la cuota supera el bloque de ocio")
    void testMetaInviableSuperaBloqueOcio() {
        Usuario usuario = new Usuario("Paolo", "paolo@test.com", new BigDecimal("2000.00"));
        usuario.setIdUsuario(1L);

        UsuarioRepositorio fakeUsuarioRepo = crearProxyUsuario(usuario);
        TransaccionRepositorio fakeTransaccionRepo = crearProxyTransaccion(Collections.emptyList());

        ServicioFinanzas servicio = new ServicioFinanzas(
                fakeUsuarioRepo, null, null, fakeTransaccionRepo, null, new ServicioCorreo()
        );

        SolicitudMetaAhorroDto solicitud = new SolicitudMetaAhorroDto(1L, "Laptop", new BigDecimal("4000.00"), 5);
        RespuestaPlanMetaDto plan = servicio.evaluarPlanMetaAhorro(solicitud);

        assertNotNull(plan);
        assertFalse(plan.isEsFactible());
        assertEquals(new BigDecimal("800.00"), plan.getCuotaMensual());
        assertNotNull(plan.getPropuestaExtensionPlazo());
        assertTrue(plan.getPlazoSugeridoViable() > 5);

        // Verificar que los nuevos límites sean estrictamente 0.00 porque 800 > 600
        for (SugerenciaRecorteDto r : plan.getRecortesSugeridos()) {
            assertEquals(new BigDecimal("0.00"), r.getNuevoLimite());
            assertNotNull(r.getLimiteBase());
            assertEquals(new BigDecimal("0.00"), r.getGastoActual());
            assertNotNull(r.getConsejo());
        }
    }

    @Test
    @DisplayName("En meta viable, nuevoLimite debe ser el saldo restante y nunca igualarse al gasto previo consumido")
    void testMetaViableConGastosPrevios() {
        Usuario usuario = new Usuario("Paolo", "paolo@test.com", new BigDecimal("2000.00"));
        usuario.setIdUsuario(1L);

        Categoria ocio = new Categoria("Ocio y gustos", "30% ocio", new BigDecimal("30.00"));
        Subcategoria subEntre = new Subcategoria("Entretenimiento", "Salidas y ocio", ocio);
        List<Transaccion> gastos = Collections.singletonList(
                new Transaccion(new BigDecimal("50.00"), LocalDate.now(), "GASTO", "Cine", subEntre, usuario)
        );

        UsuarioRepositorio fakeUsuarioRepo = crearProxyUsuario(usuario);
        TransaccionRepositorio fakeTransaccionRepo = crearProxyTransaccion(gastos);

        ServicioFinanzas servicio = new ServicioFinanzas(
                fakeUsuarioRepo, null, null, fakeTransaccionRepo, null, new ServicioCorreo()
        );

        SolicitudMetaAhorroDto solicitud = new SolicitudMetaAhorroDto(1L, "Curso", new BigDecimal("600.00"), 6);
        RespuestaPlanMetaDto plan = servicio.evaluarPlanMetaAhorro(solicitud);

        assertTrue(plan.isEsFactible());
        SugerenciaRecorteDto subRecorte = plan.getRecortesSugeridos().stream()
                .filter(r -> "Entretenimiento".equals(r.getNombreSubcategoria()))
                .findFirst().orElseThrow();

        // Limite base: 210.00
        // Gasto actual: 50.00
        // Presupuesto ajustado: 210 - 35 = 175.00
        // Saldo restante: 175 - 50 = 125.00
        assertEquals(new BigDecimal("210.00"), subRecorte.getLimiteBase());
        assertEquals(new BigDecimal("50.00"), subRecorte.getGastoActual());
        assertEquals(new BigDecimal("125.00"), subRecorte.getNuevoLimite());
        assertNotEquals(subRecorte.getGastoActual(), subRecorte.getNuevoLimite());
    }

    @Test
    @DisplayName("Si el gasto actual supera el presupuesto ajustado, nuevoLimite debe ser estrictamente 0.00")
    void testGastoActualSuperaPresupuestoAjustado() {
        Usuario usuario = new Usuario("Paolo", "paolo@test.com", new BigDecimal("2000.00"));
        usuario.setIdUsuario(1L);

        Categoria ocio = new Categoria("Ocio y gustos", "30% ocio", new BigDecimal("30.00"));
        Subcategoria subEntre = new Subcategoria("Entretenimiento", "Salidas y ocio", ocio);
        List<Transaccion> gastos = Collections.singletonList(
                new Transaccion(new BigDecimal("190.00"), LocalDate.now(), "GASTO", "Fiesta", subEntre, usuario)
        );

        UsuarioRepositorio fakeUsuarioRepo = crearProxyUsuario(usuario);
        TransaccionRepositorio fakeTransaccionRepo = crearProxyTransaccion(gastos);

        ServicioFinanzas servicio = new ServicioFinanzas(
                fakeUsuarioRepo, null, null, fakeTransaccionRepo, null, new ServicioCorreo()
        );

        SolicitudMetaAhorroDto solicitud = new SolicitudMetaAhorroDto(1L, "Curso", new BigDecimal("600.00"), 6);
        RespuestaPlanMetaDto plan = servicio.evaluarPlanMetaAhorro(solicitud);

        SugerenciaRecorteDto subRecorte = plan.getRecortesSugeridos().stream()
                .filter(r -> "Entretenimiento".equals(r.getNombreSubcategoria()))
                .findFirst().orElseThrow();

        assertEquals(new BigDecimal("210.00"), subRecorte.getLimiteBase());
        assertEquals(new BigDecimal("190.00"), subRecorte.getGastoActual());
        assertEquals(new BigDecimal("0.00"), subRecorte.getNuevoLimite());
        assertNotEquals(new BigDecimal("190.00"), subRecorte.getNuevoLimite());
        assertTrue(subRecorte.getConsejo().contains("0.00"));
    }

    @Test
    @DisplayName("Debe validar que el plazo de meses esté estrictamente entre 1 y 12")
    void testPlazoMayorA12MesesInvalido() {
        Usuario usuario = new Usuario("Paolo", "paolo@test.com", new BigDecimal("2000.00"));
        usuario.setIdUsuario(1L);

        UsuarioRepositorio fakeUsuarioRepo = crearProxyUsuario(usuario);
        TransaccionRepositorio fakeTransaccionRepo = crearProxyTransaccion(Collections.emptyList());

        ServicioFinanzas servicio = new ServicioFinanzas(
                fakeUsuarioRepo, null, null, fakeTransaccionRepo, null, new ServicioCorreo()
        );

        SolicitudMetaAhorroDto solicitud = new SolicitudMetaAhorroDto(1L, "Auto", new BigDecimal("10000.00"), 18);
        assertThrows(IllegalArgumentException.class, () -> servicio.evaluarPlanMetaAhorro(solicitud));
    }

    @Test
    @DisplayName("Debe registrar abono a la meta e incrementar montoAcumulado y calcular avance")
    void testRegistrarAbonoMeta() {
        Usuario usuario = new Usuario("Paolo", "paolo@test.com", new BigDecimal("2000.00"));
        usuario.setIdUsuario(1L);

        MetaAhorro meta = new MetaAhorro("Televisor", new BigDecimal("1200.00"), 6, new BigDecimal("200.00"), LocalDate.now(), usuario);
        meta.setIdMeta(10L);

        MetaAhorroRepositorio fakeMetaRepo = (MetaAhorroRepositorio) Proxy.newProxyInstance(
                MetaAhorroRepositorio.class.getClassLoader(),
                new Class<?>[]{MetaAhorroRepositorio.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        return Optional.of(meta);
                    }
                    if ("save".equals(method.getName())) {
                        return args[0];
                    }
                    return null;
                }
        );

        CategoriaRepositorio fakeCatRepo = (CategoriaRepositorio) Proxy.newProxyInstance(
                CategoriaRepositorio.class.getClassLoader(),
                new Class<?>[]{CategoriaRepositorio.class},
                (proxy, method, args) -> Collections.emptyList()
        );

        SubcategoriaRepositorio fakeSubRepo = (SubcategoriaRepositorio) Proxy.newProxyInstance(
                SubcategoriaRepositorio.class.getClassLoader(),
                new Class<?>[]{SubcategoriaRepositorio.class},
                (proxy, method, args) -> Collections.emptyList()
        );

        TransaccionRepositorio fakeTransRepo = (TransaccionRepositorio) Proxy.newProxyInstance(
                TransaccionRepositorio.class.getClassLoader(),
                new Class<?>[]{TransaccionRepositorio.class},
                (proxy, method, args) -> null
        );

        ServicioFinanzas servicio = new ServicioFinanzas(
                null, fakeCatRepo, fakeSubRepo, fakeTransRepo, fakeMetaRepo, new ServicioCorreo()
        );

        SolicitudAbonoMetaDto abono = new SolicitudAbonoMetaDto(1L, new BigDecimal("300.00"), LocalDate.now(), "Primer aporte");
        MetaAhorro metaActualizada = servicio.registrarAbonoMeta(10L, abono);

        assertEquals(new BigDecimal("300.00"), metaActualizada.getMontoAcumulado());
        assertEquals(new BigDecimal("900.00"), metaActualizada.getSaldoPendiente());
        assertEquals(new BigDecimal("25.0"), metaActualizada.getPorcentajeAvance());
        assertFalse(metaActualizada.isCompletada());

        // Segundo abono de 900 para completar el 100%
        servicio.registrarAbonoMeta(10L, new SolicitudAbonoMetaDto(1L, new BigDecimal("900.00"), LocalDate.now(), "Segundo aporte"));
        assertEquals(new BigDecimal("1200.00"), metaActualizada.getMontoAcumulado());
        assertEquals(BigDecimal.ZERO, metaActualizada.getSaldoPendiente());
        assertEquals(new BigDecimal("100.0"), metaActualizada.getPorcentajeAvance());
        assertTrue(metaActualizada.isCompletada());
    }
}
