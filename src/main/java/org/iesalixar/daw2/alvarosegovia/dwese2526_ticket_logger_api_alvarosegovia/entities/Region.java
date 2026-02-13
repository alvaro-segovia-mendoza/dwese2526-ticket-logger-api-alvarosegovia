package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase `Region` representa una entidad que modela una región dentro de la base de datos.
 * Contiene tres campos: `id`, `code` y `name`, donde `id` es el identificador único de la región,
 * `code` es un código asociado a la región, y `name` es el nombre de la región.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar automáticamente
 * métodos comunes como getters, setters, constructores, y otros métodos estándar de los objetos.
 */
@Data  // Esta anotación de Lombok genera automáticamente los siguientes métodos:
// - Getters y setters para todos los campos (id, code, name).
// - Los métodos `equals()` y `hashCode()` basados en todos los campos no transitorios.
// - El método `toString()` que incluye todos los campos.
// - Un método `canEqual()` que verifica si una instancia puede ser igual a otra.
// Esto evita tener que escribir manualmente todos estos métodos y mejora la mantenibilidad del código.


@NoArgsConstructor  // Esta anotación genera un constructor sin argumentos (constructor vacío),
//  es útil cuando quieres crear un objeto `Region` sin inicializarlo inmediatamente
// con valores. Esto es muy útil en frameworks como Hibernate o JPA,
// que requieren un constructor vacío para la creación de entidades.


@AllArgsConstructor  // Esta anotación genera un constructor que acepta todos los campos como parámetros (id, code, name).
// Este constructor es útil cuando necesitas crear una instancia completamente inicializada de `Region`.
// Ejemplo: new Region(1, "01", "Andalucía");
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "regions") // Especifíca el nombre de la tabla asociada a esta entidad.
public class Region {


    // Campo que almacena el identificador único de la región. Este campo suele ser autogenerado
    // por la base de datos, lo que lo convierte en un buen candidato para una clave primaria.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Campo que almacena el código de la región, normalmente una cadena corta que identifica la región.
    // Ejemplo: "01" para Andalucía.
    @Column(name = "code", nullable = false, length = 2) // Define la columna correspondiente en la tabla.
    private String code;


    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".
    @Column(name = "name", nullable = false, length = 100)
    private String name;


    /**
     * Lista de provincias pertenecientes a la región.
     * - LAZY: no se cargan hasta que se accede a 'provinces'.
     * - mappedBy: el dueño de la relación es Province.region.
     * - Con cascade ALL: Así se borrarían las provincias asociadas a la región si se elimina la región.
     */
    @OneToMany( 
            mappedBy = "region",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            orphanRemoval = false
    )
    @ToString.Exclude           // Evita bucles en toString()
    @EqualsAndHashCode.Exclude  // Evita ciclos en equals/hashCode
    private List<Province> provinces = new ArrayList<>();
}
