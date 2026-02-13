package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un rol de usuario en el sistema.
 * Un rol define permisos y/o nivel de acceso para los usuarios asociados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "users") // Excluye la relación para evitar recursión
@ToString(exclude = "users") // Excluye la relación para evitar recursión
@Entity
@Table(name = "roles")
public class Role {


    /** BIGINT AUTO_INCREMENT PRIMARY KEY */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /** VARCHAR(50) NOT NULL UNIQUE - Nombre técnico: ROLE_ADMIN, ROLE_USER... */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;


    /** VARCHAR(100) NOT NULL - Nombre legible para la interfaz */
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;


    /** VARCHAR(255) NULL - Descripción opcional del rol */
    @Column(name = "description", length = 255)
    private String description;


    /**
     * Relación N:M con User.
     * Lado NO propietario (mappedBy = "roles").
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();


    /** Constructor útil para crear roles sin id ni usuarios. */
    public Role(String name, String displayName, String description) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
    }
}

