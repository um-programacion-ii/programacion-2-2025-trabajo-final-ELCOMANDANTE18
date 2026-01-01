# 🔗 Entradera - Proxy Service

Este servicio actúa como **intermediario de seguridad** y adaptador de protocolos. Es el componente crítico encargado de aislar la infraestructura externa de la Cátedra del resto del sistema.

## 🛡️ Rol en la Arquitectura

Por diseño y seguridad, el Backend y la App Móvil **no tienen permiso** para conectarse directamente a los servicios de la Cátedra. El Proxy centraliza estas responsabilidades:

1.  **Consumidor de Kafka:** Escucha el tópico de eventos para detectar cambios (nuevos eventos o actualizaciones) y notifica al Backend.
2.  **Cliente de Redis:** Consulta el estado de los asientos (Libre/Ocupado) en tiempo real para el mapa de la App Móvil.
3.  **API Gateway:** Expone endpoints HTTP seguros que la App Móvil consume.

## 📂 Estructura del Código

Organización de paquetes basada en la responsabilidad del componente:

```text
com.tp2025.proxy
├── config/              # Configuraciones de Kafka y Redis
├── controller/          # Endpoints REST (Adaptadores de Entrada)
├── dto/                 # Data Transfer Objects (Modelos de intercambio)
├── kafka/               # Listeners y Consumers de Spring Kafka
├── service/             # Lógica de negocio (Conexión con Redis y Backend)
└── web/                 # Configuración Web/CORS
```

## ⚙️ Configuración de Entorno (.env)

Es **obligatorio** crear un archivo `.env` en la raíz de `proxy/` con las IPs de la Cátedra. Sin esto, el servicio fallará al intentar conectar.

```properties
# --- Configuración de Infraestructura Cátedra ---
# IPs provistas por la cátedra (NO usar localhost aquí)
KAFKA_BOOTSTRAP_SERVERS=192.168.194.250:9092
REDIS_HOST=192.168.194.250
REDIS_PORT=6379

# --- Configuración Local ---
# Puerto distinto al del Backend (8080) para evitar colisiones
SERVER_PORT=8081

# --- Comunicación Interna ---
# URL para notificar al Backend sobre cambios
BACKEND_URL=http://localhost:8080/api

```

## 🚀 Ejecución

El proxy utiliza Maven para la gestión de dependencias. Para iniciar el servicio cargando las variables de entorno limpiamente:

```bash
export $(grep -v '^#' .env | xargs) && mvn clean spring-boot:run
```

* **Estado:** El servicio iniciará en `http://localhost:8081`.
* **Logs:** Verás en la consola los logs de conexión a Kafka (`INFO: [Consumer clientId=...] assigned partitions...`).

## 🔌 Integración

| Dirección | Protocolo | Descripción |
| --- | --- | --- |
| **Entrada** | HTTP | Recibe peticiones del Móvil (`GET /api/proxy/ocupados/{id}`). |
| **Entrada** | Kafka TCP | Consume mensajes del tópico `eventos-topic`. |
| **Salida** | Redis TCP | Lee claves de asientos (`GET evento:{id}:asientos`). |
| **Salida** | HTTP | Envía POST al Backend cuando Kafka avisa de un cambio. |

## 🛠 Tecnologías

* **Lenguaje:** Java 21
* **Framework:** Spring Boot 3
* **Mensajería:** Spring Kafka
* **Caché/NoSQL:** Spring Data Redis (Jedis/Lettuce)
* **Build Tool:** Maven
