package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {


    /** Identificador único del token de recuperación (PK autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /** Usuario propietario del token (un usuario puede generar varios tokens a lo largo del tiempo). */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    /** Hash del token (SHA-256 en hex) para no almacenar el token en claro en base de datos. */
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash; // SHA-256 hex


    /** Fecha y hora límite hasta la que el token es válido (caducidad/TTL). */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;


    /** Fecha y hora en la que el token se consumió; si es null, aún no se ha usado. */
    @Column(name = "used_at")
    private LocalDateTime usedAt;


    /** Fecha y hora de creación del token para auditoría y trazabilidad. */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    /** IP desde la que se solicitó el reset (útil para auditoría y detección de abuso). */
    @Column(name = "request_ip", length = 45)
    private String requestIp;


    /** User-Agent del cliente que solicitó el reset para contexto y auditoría (no es un dato confiable). */
    @Column(name = "user_agent", length = 255)
    private String userAgent;


    /**
     * Indica si el token de recuperación ha caducado.
     * Un token se considera expirado cuando la fecha/hora actual es posterior a {@code expiresAt}.
     *
     * @return {@code true} si el token está caducado; {@code false} en caso contrario.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }


    /**
     * Indica si el token de recuperación ya ha sido utilizado (one-time token).
     * Un token se considera usado cuando {@code usedAt} contiene una marca temporal (no es {@code null}).
     *
     * @return {@code true} si el token ya fue consumido; {@code false} si aún está disponible.
     */
    public boolean isUsed() {
        return usedAt != null;
    }
}

