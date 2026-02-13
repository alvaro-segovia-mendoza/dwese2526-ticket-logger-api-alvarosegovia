package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La clase `Province` representa una entidad que modela una provincia dentro de la base de datos.
 * Contiene cuatro campos: `id`, `code`, `name` y `region`, donde:
 * - `id` es el identificador único de la provincia,
 * - `code` es un código asociado a la provincia,
 * - `name` es el nombre de la provincia,
 * - `region` representa la comunidad autónoma (entidad `Region`) a la que pertenece la provincia.
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
@Entity // Marca esta clase como una entidad JPA.
@Table(name = "provinces") // Define el nombre de la tabla asociada a esta entidad.
public class Province {

    // Identificador único de la provincia (AUTO_INCREMENT en la tabla 'provinces').
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único de la provincia (VARCHAR(10) NOT NULL UNIQUE).
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    // Nombre de la provincia (VARCHAR(100) NOT NULL).
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // Relación con entidad 'Region', representando la comunidad autónoma a la que pertenece la provincia.
    @ManyToOne(fetch = FetchType.LAZY) // Relación de muchas provincias a una región
    @JoinColumn(name = "region_id", nullable = false) // Clave foránea en la tabla provinces que referencia a la tabla regions.
    private Region region;

    /**
     * Constructor de conveniencia sin {@code id}
     * útil para altas donde el ID se autogenera en base de datos.
     *
     * @param code
     * @param name
     * @param region
     */
    public Province(String code, String name, Region region) {
        this.code = code;
        this.name = name;
        this.region = region;
    }



}
