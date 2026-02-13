package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API.
 * Convierte excepciones de negocio/validación en respuestas HTTP con un ApiErrorDTO homogéneo.
 */
@RestControllerAdvice
public class ApiExceptionHandler {


    /**
     * Recurso no encontrado -> 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {


        ApiErrorDTO body = ApiErrorDTO.withContext(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI(),
                ex.getResource(),
                ex.getField(),
                ex.getValue()
        );


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }


    /**
     * Duplicidad (campo único) -> 409 Conflict.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorDTO> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {


        ApiErrorDTO body = ApiErrorDTO.withContext(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI(),
                ex.getResource(),
                ex.getField(),
                ex.getValue()
        );


        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }


    /**
     * Fichero inválido -> 400 Bad Request.
     */
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidFile(InvalidFileException ex, HttpServletRequest req) {


        ApiErrorDTO body = ApiErrorDTO.withContext(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI(),
                ex.getResource(),
                ex.getField(),
                ex.getValue()
        );


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    /**
     * Errores de validación de @Valid -> 400 Bad Request con errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {


        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }


        // ApiErrorDTO.validation ya fija el 400 y el texto estándar
        ApiErrorDTO body = ApiErrorDTO.validation(req.getRequestURI(), fieldErrors);


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    /**
     * JSON mal formado o tipos incompatibles -> 400 Bad Request.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {


        ApiErrorDTO body = ApiErrorDTO.basic(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "JSON inválido o mal formado",
                req.getRequestURI()
        );


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    /**
     * Cualquier error no controlado -> 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneric(Exception ex, HttpServletRequest req) {


        ApiErrorDTO body = ApiErrorDTO.basic(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Error interno al procesar la solicitud",
                req.getRequestURI()
        );


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

