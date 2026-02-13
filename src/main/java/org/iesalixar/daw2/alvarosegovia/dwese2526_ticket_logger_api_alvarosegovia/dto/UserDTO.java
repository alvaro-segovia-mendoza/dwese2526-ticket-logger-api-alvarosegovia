package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object (DTO) para la entidad User.
 * Se utiliza para transferir información de usuario entre capas,
 * sin exponer directamente la entidad ni datos sensibles innecesarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {


    private Long id;


    private String email;


    private boolean active;


    private boolean accountNonLocked;


    private LocalDateTime lastPasswordChange;


    private LocalDateTime passwordExpiresAt;


    private Integer failedLoginAttempts;


    private boolean emailVerified;


    private boolean mustChangePassword;


    // Roles asociados al usuario (nombres técnicos: ROLE_ADMIN, ROLE_USER, etc.)
    private Set<String> roles;
}

