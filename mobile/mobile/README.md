# 📱 Entradera - Cliente Móvil

Cliente nativo Android desarrollado con **Kotlin Multiplatform (KMP)** y **Jetpack Compose**. Esta aplicación implementa patrones de diseño modernos para garantizar la escalabilidad y una experiencia de usuario fluida.

## 🎨 Arquitectura: MVVM + Clean Architecture

El proyecto ha sido refactorizado para seguir el patrón **Model-View-ViewModel**, desacoplando totalmente la interfaz gráfica de la lógica de negocio y de red.

### Componentes de la Arquitectura
1.  **View (`ui/screens`):** Componentes visuales "tontos" desarrollados en Compose. No contienen lógica de negocio, solo observan estados y emiten eventos.
2.  **ViewModel (`ui/viewmodel`):** (NUEVO) Gestionan el estado de la UI (`StateFlow` / `MutableState`) y actúan como intermediarios entre la Vista y la Capa de Datos.
3.  **Model (`domain.model`):** Clases de datos puras que representan el negocio (`Evento`, `AsientoVenta`).
4.  **Data (`data/network`, `data/local`):** Repositorios encargados de obtener datos del Backend, del Proxy o del almacenamiento local.

## 📂 Estructura del Código

Basado en la estructura actual del proyecto:

```text
com.tp2025.mobile
├── auth/                # Lógica específica de autenticación
├── data/                # Capa de Datos (Data Layer)
│   ├── local/           # Persistencia local (SessionManager para Tokens)
│   └── network/         # Clientes HTTP (KtorClient, EventoService, ProxyRepository)
├── domain.model/        # Entidades de Negocio (Core)
│   ├── Evento.kt
│   └── AsientoVenta.kt
├── ui/                  # Capa de Presentación (UI Layer)
│   ├── screens/         # Pantallas (AsientosScreen, DetalleVentaScreen, etc.)
│   └── viewmodel/       # Lógica de presentación (AsientosViewModel, DetalleVentaViewModel)
└── App.kt               # Punto de entrada y Grafo de Navegación

```

## 🔌 Conectividad e Integración

La aplicación interactúa con dos servicios distintos, configurados en `data/network`:

| Servicio | Puerto Local | Función Principal |
| --- | --- | --- |
| **Backend** | `:8080` | Autenticación (Login/Registro) y Transacción de Venta. |
| **Proxy** | `:8081` | Consulta de mapa de asientos (Redis) y Bloqueos temporales. |

## 🛠 Tecnologías y Librerías

* **UI Toolkit:** Jetpack Compose Multiplatform.
* **Lenguaje:** Kotlin.
* **Networking:** Ktor Client (ContentNegotiation, Serialization).
* **Concurrencia:** Kotlin Coroutines.
* **Arquitectura:** MVVM (Model-View-ViewModel).

## 🚀 Ejecución

Para correr la aplicación en un emulador o dispositivo físico:

1. Asegúrate de tener corriendo el **Backend** y el **Proxy** (ver README principal).
2. Abre el proyecto en **Android Studio**.
3. Selecciona la configuración de ejecución `composeApp`.
4. Presiona **Run** (▶).

> **Nota:** Si usas un dispositivo físico, asegúrate de que el celular y tu PC estén en la misma red Wi-Fi y actualiza las IPs en `EventoService.kt`.
