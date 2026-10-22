# Gestor de Finanzas Personales 💰 (Regla 70-30-20)

Una aplicación web desarrollada en **Java con Spring Boot** y **PostgreSQL** para la gestión inteligente del presupuesto personal, control de gastos en cascada y planificación de metas de ahorro.

---

## 📌 El Problema

A muchas personas y estudiantes universitarios se les hace difícil llevar un control claro de cuánto dinero ganan y en qué se les va cada mes. Los problemas más comunes suelen ser:

1. **Falta de visibilidad:** No saber con certeza cuánto dinero queda disponible tras pagar los gastos fijos del hogar o los estudios.
2. **Gastos impulsivos en ocio:** Salidas, compras personales y suscripciones que terminan absorbiendo dinero que estaba destinado a necesidades básicas o al ahorro.
3. **Metas inalcanzables o mal calculadas:** Querer comprar algo importante (una laptop para la universidad, un mueble, un viaje) fijando plazos poco realistas que obligan a endeudarse o a descuidar pagos esenciales como vivienda o alimentación.

---

## 💡 La Solución

Este gestor implementa la **Regla Presupuestaria 70-30-20**, organizando las finanzas en tres bloques jerárquicos claros y protegiendo el bienestar económico del usuario:

* **70% — Necesidades Básicas (Gastos Fijos Indispensables):**
  Vivienda, Servicios Básicos, Alimentación, Salud, Educación, Transporte y Préstamos. **Este bloque queda 100% blindado** y el sistema no permite recortar de aquí para cumplir metas recreativas.
* **30% — Ocio y Gustos (Gastos Prescindibles):**
  Entretenimiento, Compras Personales, Suscripciones de Streaming y Viajes de Placer. De aquí se optimizan y extraen los recortes cuando el usuario se propone una meta de ahorro.
* **20% — Ahorro e Inversión:**
  Fondo de emergencia e Inversiones a plazo fijo o fondos mutuos.

### Módulos Principales:
* **Flujo Inicial Limpio:** Comienza preguntando el ingreso mensual base y habilita el panel de control sin datos basura ni gastos precargados.
* **Monitoreo con Acordeón Interactivo:** Muestra el porcentaje consumido en cada categoría padre y permite desplegar el detalle de cada subcategoría con barras de progreso en tiempo real.
* **Validación de Servicios Básicos:** Compara el recibo actual (luz, agua, internet, etc.) con el histórico anterior y avisa si el consumo subió, bajó o se mantuvo estable.
* **Planificador Inteligente de Metas:** Calcula la cuota mensual para plazos de 1 a 12 meses. Si la cuota requerida supera el bloque de ocio, alerta inmediatamente al usuario y propone ampliar el plazo en meses viables.
* **Módulo de Depósitos y Seguimiento:** Permite registrar aportes a la meta activa, mostrando en tiempo real el monto acumulado, el saldo pendiente y el avance porcentual hasta completar el 100%.
* **Despacho de Reportes:** Genera un resumen financiero completo con diagnóstico de recortes listo para enviar al correo del usuario.

---

## 🛠️ Tecnologías y Lenguajes Utilizados

* **Backend:**
  * **Java 17+**
  * **Spring Boot 3.2** (Spring Web, Spring Data JPA)
  * **Hibernate ORM** (mapeo relacional y validaciones)
  * **Maven** (gestor de dependencias y construcción)
  * **JUnit 5** (pruebas unitarias para la lógica matemática de presupuestos)
* **Base de Datos:**
  * **PostgreSQL** (diseño relacional con claves foráneas, restricciones CHECK e índices)
* **Frontend:**
  * **HTML5** semántico
  * **CSS3** (diseño responsivo, animaciones, componentes en acordeón y modales)
  * **JavaScript (Vanilla JS)** con Fetch API para comunicación asíncrona REST
* **Herramientas de Desarrollo:**
  * Apache NetBeans / IntelliJ IDEA / VS Code
  * Git y GitHub

---

## 🏗️ Arquitectura del Proyecto

El código está estructurado bajo el patrón de arquitectura por capas (N-Tier), manteniendo alta cohesión y bajo acoplamiento:

`
src/main/java/com/universidad/gestorfinanzas/
├── controlador/          # Endpoints REST y manejador global de excepciones (@RestControllerAdvice)
├── servicio/             # Lógica de negocio (regla 70-30-20, optimización de metas, catálogo)
├── repositorio/          # Interfaces Spring Data JPA para acceso a PostgreSQL
├── modelo/               # Entidades JPA (Usuario, Categoria, Subcategoria, Transaccion, MetaAhorro)
├── dto/                  # Objetos de transferencia de datos para solicitudes y respuestas JSON
└── GestorFinanzasAplicacion.java  # Clase principal de arranque Spring Boot
`

---

## 🚀 Cómo Ejecutar el Proyecto en Local

### 1. Prerrequisitos
* Tener instalado **Java JDK 17 o superior**.
* Tener instalado **PostgreSQL** y un cliente como pgAdmin o psql.
* Tener instalado **Apache Maven** (o usar el Maven integrado en NetBeans/IntelliJ).

### 2. Configurar la Base de Datos
1. Abre PostgreSQL y crea la base de datos:
   `sql
   CREATE DATABASE finanzas_db;
   `
2. Ejecuta el script [esquema.sql](esquema.sql) para crear las tablas y cargar las categorías maestras de la regla 70-30-20.

### 3. Configurar Credenciales
Revisa el archivo src/main/resources/application.properties con tus credenciales locales:
`properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/finanzas_db
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASEÑA
`

### 4. Compilar y Ejecutar
Desde la raíz del proyecto, ejecuta en la terminal:
`ash
mvn clean test
mvn spring-boot:run
`
O ábrelo directamente en **Apache NetBeans** y presiona **Run Project (F6)**.

### 5. Abrir la Aplicación
Ingresa en tu navegador web a:
👉 http://localhost:8080

---

## 👤 Autor
* **Paolo** - *Estudiante de Ingeniería de Software*
* GitHub: [@Paoloo0](https://github.com/Paoloo0)