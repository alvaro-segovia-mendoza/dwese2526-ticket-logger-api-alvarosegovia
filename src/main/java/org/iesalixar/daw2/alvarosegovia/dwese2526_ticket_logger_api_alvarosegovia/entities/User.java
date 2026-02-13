package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * La clase `User` representa una entidad que modela un user dentro de la base de datos.
 * Contiene variso campos: donde `id` es el identificador único de la región,
 * `code` es un código asociado a la región, y `name` es el nombre de la región.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar automáticamente
 * métodos comunes como getters, setters, constructores, y otros métodos estándar de los objetos.
 */
@Data
// Genera automáticamente los métodos:
// - Getters y setters para todos los campos.
// - equals(), hashCode(), toString() y canEqual().
// Esto mejora la legibilidad y evita código repetitivo.

@NoArgsConstructor
// Genera un constructor vacío (sin parámetros).
// Es necesario para frameworks como Hibernate o JPA,
// que requieren un constructor por defecto para instanciar objetos.

@AllArgsConstructor
// Genera un constructor que acepta todos los campos definidos en la clase.
// Ideal para crear instancias completamente inicializadas de la entidad.
@Entity // Marca esta clase como entidad JPA
@Table(name = "users") // Define el nombre de la tabla en la base de datos
public class User {


    /** BIGINT AUTO_INCREMENT PRIMARY KEY */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /** VARCHAR(100) NOT NULL UNIQUE */
    @Column(name = "email", nullable = false, unique = true, length = 40)
    private String email;


    /** VARCHAR(500) NOT NULL */
    @Column(name = "password_hash", nullable = false, length = 500)
    private String passwordHash;


    /** BOOLEAN NOT NULL DEFAULT TRUE */
    @Column(name = "active", nullable = false)
    private boolean active;


    /** BOOLEAN NOT NULL DEFAULT TRUE */
    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked;


    /** DATETIME NULL */
    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;


    /** DATETIME NULL */
    @Column(name = "password_expires_at")
    private LocalDateTime passwordExpiresAt;


    /** INT DEFAULT 0 */
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;


    /** BOOLEAN NOT NULL DEFAULT FALSE */
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;


    /** BOOLEAN NOT NULL DEFAULT FALSE */
    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;


    /** Relación 1:1 con la entidad UserProfile */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserProfile profile;


    /** Relación N:M con Role a través de la tabla intermedia 'user_roles'. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();




    /** Constructor completo (sin id autogenerado). */
    public User(String username,
                String passwordHash,
                Boolean active,
                Boolean accountNonLocked,
                LocalDateTime lastPasswordChange,
                LocalDateTime passwordExpiresAt,
                Integer failedLoginAttempts,
                Boolean emailVerified,
                Boolean mustChangePassword) {
        this.email = username;
        this.passwordHash = passwordHash;
        this.active = active;
        this.accountNonLocked = accountNonLocked;
        this.lastPasswordChange = lastPasswordChange;
        this.passwordExpiresAt = passwordExpiresAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.emailVerified = emailVerified;
        this.mustChangePassword = mustChangePassword;
    }
}
