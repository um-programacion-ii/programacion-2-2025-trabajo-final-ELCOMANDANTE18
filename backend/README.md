# ☕ Entradera - Backend Service

Este es el servicio principal (Core) del sistema, generado con **JHipster** y personalizado para gestionar la lógica de negocio, autenticación y persistencia de datos.

## 🏛 Arquitectura Hexagonal

El proyecto sigue una arquitectura en capas que respeta los principios de desacoplamiento hexagonal, visible en la estructura de paquetes:

* **`domain` (Núcleo):** Contiene las entidades JPA puras (`Evento`, `Venta`, `User`). No dependen de la infraestructura.
* **`web.rest` (Adaptadores de Entrada):** Controladores REST que exponen la API. Reciben peticiones HTTP y las transforman para el dominio.
* **`service` (Lógica de Aplicación):** Orquesta los casos de uso. Utiliza **DTOs** para transferir datos y **Mappers** para convertir entre DTO y Entidad.
* **`repository` (Adaptadores de Salida):** Interfaces que implementan la persistencia en base de datos (MySQL) usando Spring Data JPA.

## 📂 Estructura del Código

Basado en la estructura actual del proyecto:

```text
com.mycompany.myapp
├── config/              # Configuraciones de Spring (Cache, Database, Security)
├── domain/              # Entidades JPA (Modelos de Base de Datos)
├── repository/          # Repositorios (Acceso a Datos)
├── security/            # Lógica de seguridad JWT y Spring Security
├── service/
│   ├── dto/             # Data Transfer Objects (UserDTO, AdminUserDTO)
│   ├── mapper/          # MapStruct Mappers (UserMapper)
│   ├── EventoService    # Lógica de negocio de eventos
│   └── MailService      # Servicio de notificaciones
└── web.rest/            # API Endpoints
    ├── vm/              # View Models (LoginVM)
    ├── AuthenticateController  # Login y emisión de JWT
    ├── EventoResource          # CRUD de Eventos
    └── VentaResource           # Proceso de Venta

```
## ⚙️ Configuración de Entorno (.env)

Para ejecutar el proyecto, es **obligatorio** crear un archivo `.env` en la raíz de `backend/` con las siguientes variables:

```properties
# Configuración de Base de Datos
DB_NAME=eventos_BD
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña

# Seguridad JWT (JHipster)
JWT_SECRET=MiClaveSecretaSuperLargaParaJWTQueDebeSerDeAlMenos64CaracteresParaQueSeaSegura2025
JWT_EXPIRATION=86400

# Integración Externa
CATEDRA_TOKEN=tu_token_de_catedra_aqui
PROXY_URL=http://localhost:8081
```

## 🚀 Ejecución

El backend utiliza Maven Wrapper. Para iniciarlo cargando las variables del archivo `.env`:

```bash
export $(grep -v '^#' .env | xargs) && ./mvnw
```

* El servidor iniciará en: `http://localhost:8080`

## 🛠 Tecnologías

* **Framework:** Spring Boot 3.x
* **Generador:** JHipster
* **Base de Datos:** MySQL / MariaDB
* **Cache:** Redis (Configurado en `CacheConfiguration`)
* **Seguridad:** Spring Security + JWT
* **Mapeo:** MapStruct

