package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Implementación por propiedades: construye URLs públicas usando una base configurada
 * (dominio/puerto/esquema) definida en {@code application.properties}.
 * <p>
 * Recomendación: en producción esta base debe ser el dominio real público tras proxy/CDN.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AppUrlServiceImpl implements AppUrlService {


    /**
     * Base pública de la aplicación, por ejemplo:
     * <ul>
     *   <li>{@code https://ticket-logger.tudominio.com}</li>
     *   <li>{@code https://www.iaph.es/ticket-logger}</li>
     * </ul>
     */
    @Value("${app.public-base-url}")
    private String publicBaseUrl;


    /**
     * Ruta del endpoint público del reset (GET muestra formulario).
     * Ajusta este valor a tu controller real.
     */
    @Value("${app.password-reset.path:/users/reset-password}")
    private String resetPath;


    /**
     * Construye la URL pública (absoluta) para el formulario de reset de contraseña.
     *
     * @param rawToken token en claro que viajará como parámetro de la URL.
     * @return URL absoluta lista para enviar por email.
     */
    @Override
    public String buildResetUrl(String rawToken) {
        return buildUrl(resetPath, Map.of("token", rawToken));
    }


    /**
     * Construye una URL absoluta a partir de una ruta relativa y parámetros de query.
     *
     * @param path        ruta relativa (ej. "/users/reset-password").
     * @param queryParams parámetros de query (ej. {"token":"..."}).
     * @return URL absoluta completa.
     */
    @Override
    public String buildUrl(String path, Map<String, String> queryParams) {
        UriComponentsBuilder b = UriComponentsBuilder
                .fromUriString(trimTrailingSlash(publicBaseUrl))
                .path(ensureLeadingSlash(path));


        if (queryParams != null) {
            queryParams.forEach(b::queryParam);
        }
        return b.build(true).toUriString(); // true => encode de forma segura
    }


    private String trimTrailingSlash(String s) {
        if (s == null || s.isBlank()) {
            throw new IllegalStateException("app.public-base-url no está configurada.");
        }
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }


    private String ensureLeadingSlash(String p) {
        if (p == null || p.isBlank()) return "/";
        return p.startsWith("/") ? p : "/" + p;
    }
}

