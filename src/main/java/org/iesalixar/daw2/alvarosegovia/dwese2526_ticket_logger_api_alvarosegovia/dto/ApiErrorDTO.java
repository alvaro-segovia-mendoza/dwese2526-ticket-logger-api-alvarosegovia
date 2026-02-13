package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO estándar para devolver errores en la API (formato homogéneo).
 * <p>
 * Idea clave: todos los errores de la API devuelven el mismo "shape" JSON, para que el frontend
 * pueda tratarlos siempre igual (mostrar mensaje, resaltar campos, etc.).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorDTO {


    /** Momento en que se genera la respuesta de error (útil para logs y depuración). */
    private Instant timestamp;


    /** Código HTTP numérico (400, 404, 409, 500...). */
    private int status;


    /** Texto asociado al estado HTTP (ej. "Bad Request", "Not Found", "Conflict"). */
    private String error;


    /** Mensaje principal legible (puede ser genérico o derivado de la excepción). */
    private String message;


    /** Ruta del endpoint que provocó el error (ej. "/api/regions/10"). */
    private String path;


    /** Recurso afectado (ej. "region", "user"). Útil en errores semánticos. */
    private String resource;


    /** Campo relacionado con el error (ej. "id", "code", "email"). */
    private String field;


    /** Valor del campo que causó el error (ej. 10, "AND"). */
    private Object value;


    /** Errores por campo cuando falla la validación (@Valid). Ej.: {"code":"no puede estar vacío"}. */
    private Map<String, String> fieldErrors;




    // =========================================================================
    // FÁBRICAS ESTÁTICAS (helpers para construir respuestas de error)
    // =========================================================================


    /**
     * Fábrica para errores "básicos" sin contexto (sin resource/field/value ni fieldErrors).
     * <p>
     * Caso típico: 500 Internal Server Error genérico, o un 400 simple sin detalle por campo.
     * </p>
     */
    public static ApiErrorDTO basic(int status, String error, String message, String path) {


        Instant now = Instant.now();


        return new ApiErrorDTO(
                now,
                status,
                error,
                message,
                path,


                // contexto semántico (no aplica en basic)
                null,
                null,
                null,


                // validación por campos (no aplica en basic)
                null
        );
    }


    /**
     * Fábrica para errores con contexto semántico (resource/field/value).
     * <p>
     * Casos típicos:
     * - 404 Not Found (resource="region", field="id", value=99)
     * - 409 Conflict por duplicidad (resource="region", field="code", value="AND")
     * - 400 Bad Request semántico (ej. fichero inválido)
     * </p>
     */
    public static ApiErrorDTO withContext(int status, String error, String message, String path,
                                          String resource, String field, Object value) {


        Instant now = Instant.now();


        return new ApiErrorDTO(
                now,
                status,
                error,
                message,
                path,


                // contexto semántico
                resource,
                field,
                value,


                // validación por campos (no aplica aquí)
                null
        );
    }


    /**
     * Fábrica específica para errores de validación (@Valid).
     * <p>
     * Devuelve siempre 400 Bad Request y rellena fieldErrors.
     * El frontend puede pintar mensajes por campo directamente.
     * </p>
     */
    public static ApiErrorDTO validation(String path, Map<String, String> fieldErrors) {


        Instant now = Instant.now();


        return new ApiErrorDTO(
                now,
                400,
                "Bad Request",
                "Validación fallida",
                path,


                // contexto semántico (no aplica en validación)
                null,
                null,
                null,


                // errores por campo
                fieldErrors
        );
    }
}

