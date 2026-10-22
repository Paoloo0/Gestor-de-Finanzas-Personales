package com.universidad.gestorfinanzas.controlador;

import com.universidad.gestorfinanzas.dto.ActualizarIngresoDto;
import com.universidad.gestorfinanzas.dto.RegistroUsuarioDto;
import com.universidad.gestorfinanzas.dto.RespuestaEnvioCorreoDto;
import com.universidad.gestorfinanzas.dto.RespuestaPlanMetaDto;
import com.universidad.gestorfinanzas.dto.RespuestaTransaccionDto;
import com.universidad.gestorfinanzas.dto.ResumenMensualDto;
import com.universidad.gestorfinanzas.dto.SolicitudAbonoMetaDto;
import com.universidad.gestorfinanzas.dto.SolicitudMetaAhorroDto;
import com.universidad.gestorfinanzas.dto.SolicitudTransaccionDto;
import com.universidad.gestorfinanzas.modelo.Categoria;
import com.universidad.gestorfinanzas.modelo.MetaAhorro;
import com.universidad.gestorfinanzas.modelo.Subcategoria;
import com.universidad.gestorfinanzas.modelo.Transaccion;
import com.universidad.gestorfinanzas.modelo.Usuario;
import com.universidad.gestorfinanzas.servicio.ServicioFinanzas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ControladorFinanzas {

    private final ServicioFinanzas servicioFinanzas;

    public ControladorFinanzas(ServicioFinanzas servicioFinanzas) {
        this.servicioFinanzas = servicioFinanzas;
    }

    /**
     * Paso 1: Registrar usuario inicial con su ingreso mensual.
     */
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody RegistroUsuarioDto dto) {
        Usuario usuario = servicioFinanzas.registrarUsuarioConIngreso(dto);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    /**
     * Consultar si ya existe un usuario registrado (para saber qué vista mostrar al abrir).
     */
    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(servicioFinanzas.obtenerUsuario(idUsuario));
    }

    /**
     * Actualizar el ingreso mensual base del usuario.
     */
    @PutMapping("/usuarios/{idUsuario}/ingreso")
    public ResponseEntity<Usuario> actualizarIngresoMensual(
            @PathVariable Long idUsuario,
            @RequestBody ActualizarIngresoDto dto) {
        Usuario usuarioActualizado = servicioFinanzas.actualizarIngresoMensual(idUsuario, dto.getNuevoIngreso());
        return ResponseEntity.ok(usuarioActualizado);
    }

    /**
     * Paso 2: Registrar un gasto o ingreso asignado a una subcategoría.
     */
    @PostMapping("/transacciones")
    public ResponseEntity<RespuestaTransaccionDto> registrarTransaccion(@RequestBody SolicitudTransaccionDto solicitud) {
        RespuestaTransaccionDto respuesta = servicioFinanzas.registrarTransaccion(solicitud);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    /**
     * Consultar el balance mensual jerárquico (Categorías principales y subcategorías).
     */
    @GetMapping("/finanzas/resumen/{idUsuario}")
    public ResponseEntity<ResumenMensualDto> obtenerResumenMensual(
            @PathVariable Long idUsuario,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {

        int mesConsulta = (mes != null) ? mes : LocalDate.now().getMonthValue();
        int anioConsulta = (anio != null) ? anio : LocalDate.now().getYear();

        ResumenMensualDto resumen = servicioFinanzas.obtenerResumenMensual(idUsuario, mesConsulta, anioConsulta);
        return ResponseEntity.ok(resumen);
    }

    /**
     * Listar categorías principales con sus subcategorías para selectores en cascada.
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(servicioFinanzas.listarCategorias());
    }

    /**
     * Listar subcategorías por categoría padre.
     */
    @GetMapping("/subcategorias/categoria/{idCategoria}")
    public ResponseEntity<List<Subcategoria>> listarSubcategorias(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(servicioFinanzas.listarSubcategoriasPorCategoria(idCategoria));
    }

    /**
     * Historial de transacciones del usuario.
     */
    @GetMapping("/transacciones/usuario/{idUsuario}")
    public ResponseEntity<List<Transaccion>> listarTransacciones(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(servicioFinanzas.listarTransaccionesUsuario(idUsuario));
    }

    /**
     * Lógica Especial: Evaluar Meta de Ahorro y sugerir recortes en el 30% (Ocio y gustos).
     */
    @PostMapping("/metas/evaluar")
    public ResponseEntity<RespuestaPlanMetaDto> evaluarMetaAhorro(@RequestBody SolicitudMetaAhorroDto solicitud) {
        RespuestaPlanMetaDto plan = servicioFinanzas.evaluarPlanMetaAhorro(solicitud);
        return ResponseEntity.ok(plan);
    }

    /**
     * Guardar una meta de ahorro confirmada por el usuario.
     */
    @PostMapping("/metas/guardar")
    public ResponseEntity<MetaAhorro> guardarMetaAhorro(@RequestBody SolicitudMetaAhorroDto solicitud) {
        MetaAhorro metaGuardada = servicioFinanzas.guardarMetaAhorro(solicitud);
        return new ResponseEntity<>(metaGuardada, HttpStatus.CREATED);
    }

    /**
     * Listar metas de ahorro del usuario.
     */
    @GetMapping("/metas/usuario/{idUsuario}")
    public ResponseEntity<List<MetaAhorro>> listarMetas(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(servicioFinanzas.listarMetasUsuario(idUsuario));
    }

    /**
     * Paso 3 / Módulo de Depósitos: Registrar abono a una meta de ahorro.
     */
    @PostMapping("/metas/{idMeta}/abono")
    public ResponseEntity<MetaAhorro> registrarAbonoMeta(
            @PathVariable Long idMeta,
            @RequestBody SolicitudAbonoMetaDto dto) {
        MetaAhorro metaActualizada = servicioFinanzas.registrarAbonoMeta(idMeta, dto);
        return ResponseEntity.ok(metaActualizada);
    }

    /**
     * Despacha el reporte formal de la meta de ahorro por correo electrónico al usuario.
     */
    @PostMapping("/metas/{idMeta}/enviar-correo")
    public ResponseEntity<RespuestaEnvioCorreoDto> enviarReportePorCorreo(
            @PathVariable Long idMeta,
            @RequestParam Long idUsuario) {
        RespuestaEnvioCorreoDto respuesta = servicioFinanzas.enviarReporteMetaPorCorreo(idUsuario, idMeta);
        return ResponseEntity.ok(respuesta);
    }
}