package com.mycompany.myapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    // --- JHipster Default ---
    private final Liquibase liquibase = new Liquibase();

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public static class Liquibase {
        private Boolean asyncStart = true;
        public Boolean getAsyncStart() { return asyncStart; }
        public void setAsyncStart(Boolean asyncStart) { this.asyncStart = asyncStart; }
    }

    // --- NUESTRA CONFIGURACIÓN (Actualizada) ---

    private final Catedra catedra = new Catedra();
    private final Proxy proxy = new Proxy();

    public Catedra getCatedra() { return catedra; }
    public Proxy getProxy() { return proxy; }

    // Clase Cátedra: Ahora con URL y Token (Igual que tu amigo)
    public static class Catedra {
        private String token;
        private String baseUrl; // <--- NUEVO

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }

    // Clase Proxy
    public static class Proxy {
        private String url;
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}
