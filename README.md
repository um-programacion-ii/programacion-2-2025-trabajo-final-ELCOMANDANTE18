<div align="center">
  <img src="docs/logo.entradera.png" alt="Logo Entradera" width="300">
  <br>
  <h1>Entradera</h1>
  <p>Sistema integral para el registro y gestión de asistencia a eventos únicos.</p>
</div>

## 📋 Descripción
**Trabajo Final - Programación 2 - 2025**

Sistema distribuido que permite a los usuarios consultar eventos, visualizar mapas de asientos en tiempo real y realizar la compra de entradas de manera segura. El sistema implementa una arquitectura de microservicios para garantizar la consistencia de datos, interactuando con una infraestructura externa provista por la Cátedra (API, Kafka y Redis).

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

## 🏗 Arquitectura del Sistema

El proyecto está dividido en cuatro componentes principales:

1) Backend: (Java/JHipster): API REST principal que centraliza la lógica de negocio. Es responsable de la persistencia local de ventas en MySQL e interactúa directamente con el cliente móvil.

2) Proxy: (Java/Spring Boot): Servicio intermediario que actúa como barrera de seguridad. Funciona como el único punto de acceso autorizado a los servicios de Kafka y Redis de la Cátedra.

3) Cliente Móvil: (Kotlin Multiplatform): Interfaz gráfica nativa desarrollada para Android. Implementa una arquitectura MVVM para gestionar la interacción del usuario con el sistema.

4) Servicio Cátedra: Infraestructura externa provista por la cátedra. Expone los servicios de gestión de eventos, notificaciones y el estado de los asientos.

### Diagrama de Componentes

```text
┌─────────────────────────────────────────────────────────────────┐
│                   SERVICIOS DE CÁTEDRA                          │
│  ┌─────────────────┐             ┌─────────────────┐            │
│  │   API Cátedra   │             │                 │            │
│  │     :8080       │             │                 │            │
│  └────────┬────────┘             │                 │            │
│           │                      │                 │            │
│  ┌────────┴────────┐             │                 │            │
│  │     Kafka       │             │     Redis       │            │
│  │     :9092       │             │     :6379       │            │
│  └────────┬────────┘             └────────┬────────┘            │
└───────────│───────────────────────────────│─────────────────────┘
            │                               │
            │  ┌──────────────────────────┐ │
            └─►│          PROXY           │◄┘
               │         :8081            │
               └────────────┬─────────────┘
                            │
                            │ HTTP
                            ▼
               ┌──────────────────────────┐
               │         BACKEND          │
               │         :8080            │
               └────────────┬─────────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ▼             │             ▼
        ┌──────────┐        │       ┌──────────┐
        │ MariaDB  │        │       │  Mobile  │
        │  :3306   │        │       │   App    │
        └──────────┘        │       └──────────┘
                            │
                            ▼
                   ┌──────────────┐
                   │   Usuario    │
                   │    Final     │
                   └──────────────┘

```

## ⚙️ Configuración de Entorno (.env)

Antes de iniciar, es **obligatorio** crear un archivo `.env` en la raíz de las carpetas `backend/` y `proxy/`.

**Datos de Conexión Cátedra (IPs Reales):**
| Servicio | IP | Puerto |
| :--- | :--- | :--- |
| **API Cátedra** | `192.168.194.250` | 8080 |
| **Kafka** | `192.168.194.250` | 9092 |
| **Redis** | `192.168.194.250` | 6379 |

*Ejemplo de contenido para `proxy/.env`:*

```properties
KAFKA_BOOTSTRAP_SERVERS=192.168.194.250:9092
REDIS_HOST=192.168.194.250
REDIS_PORT=6379
SERVER_PORT=8081
```

## 🚀 Guía de Ejecución

Sigue este orden estricto para levantar el sistema completo:

### 1. Infraestructura Local (Docker)

Levanta la base de datos MySQL y el Redis local (para sesiones de usuario).

```bash
cd backend/src/main/docker
docker compose up -d
```

### 2. Iniciar el Backend

En una nueva terminal, inicia el servicio principal cargando las variables de entorno:

```bash
cd backend
export $(grep -v '^#' .env | xargs) && ./mvnw
```

*El servicio estará disponible en `http://localhost:8080`.*

### 3. Iniciar el Proxy

En otra terminal, inicia el intermediario con la cátedra:

```bash
cd proxy
export $(grep -v '^#' .env | xargs) && mvn clean spring-boot:run
```

*El servicio estará disponible en `http://localhost:8081`.*

### 4. Cliente Móvil

1. Abrir el proyecto en **Android Studio**.
2. Sincronizar Gradle.
3. Ejecutar la configuración `composeApp` en un emulador o dispositivo físico.

## 🛠 Tecnologías Utilizadas

* **Backend**: Java 21, Spring Boot, JHipster, MySQL, Hibernate.
* **Proxy**: Java 21, Spring Boot, Spring Kafka, Spring Data Redis.
* **Mobile**: Kotlin Multiplatform (KMP), Jetpack Compose, MVVM Pattern.
* **Infraestructura**: Docker, Docker Compose.

## 👤 Autor

**Nombre:** Victor Benjamin GIMENEZ
**Legajo:** 61174

## 📄 Licencia

Proyecto académico para la Universidad de Mendoza.
