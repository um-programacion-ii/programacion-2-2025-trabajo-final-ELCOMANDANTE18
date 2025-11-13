# Sistema de Gestión de Asistencia a Eventos

Sistema integral para el registro y gestión de asistencia a eventos únicos, como parte del Trabajo de Regularización 2025.

**Trabajo Final - Programación 2 - 2025**

## Descripción

Sistema que permite a los usuarios:
- Consultar el listado de eventos disponibles
- Ver el detalle y el mapa de asientos de un evento
- Seleccionar y bloquear temporalmente (por sesión) de 1 a 4 asientos[cite: 59, 164].
- Cargar los datos (nombre y apellido) de los asistentes para los asientos seleccionados[cite: 60].
- Confirmar la compra (venta) de las entradas.

## Arquitectura

El proyecto está dividido en cuatro componentes principales según el enunciado:

1. **Backend** - (Java/JHipster): API REST principal que gestiona la lógica de negocio, se comunica con el cliente móvil y el proxy.
2. **Proxy** - (Java/Spring Boot): Servicio intermediario que es el **único** con acceso al Kafka y Redis de la Cátedra.
3. **Cliente Móvil** - (Kotlin Multiplatform): Aplicación móvil para los usuarios finales.
4. **Servicio Cátedra** - (Provisto por la cátedra): Expone endpoints, un topic de Kafka para cambios y un Redis para el estado de los asientos.



## Estructura del Proyecto

```
.
├── backend/              # Backend Spring Boot
├── proxy/                # Servicio Proxy
├── mobile/               # Cliente Móvil
├── docs/                 # Documentación técnica
├── scripts/              # Scripts de utilidades
├── docker-compose.yml    # Configuración de servicios
└── README.md
```



## Tecnologías

-   **Backend**: Java (Spring Boot / JHipster) [cite: 34], MySQL, Redis (para sesiones locales).
-   **Proxy**: Java (Spring Boot), Kafka Consumer, Redis Client.
-   **Mobile**: Kotlin Multiplatform (KMP).
-   **Infraestructura**: Docker.

## Requisitos

-   Java JDK 17+
-   Docker Desktop
-   Git

## Estado del Proyecto

Proyecto en fase inicial. Estructura de repositorio definida y planificación de hitos en progreso.

## Autor

**Nombre:** Victor Benjamin GIMENEZ

**Legajo:** 61174

## Licencia

Proyecto académico.

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/IEOUmR9z)
