package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repositorio de acceso a datos para la entidad {@link Role}.
 * <p>
 * Extiende {@link JpaRepository} para proporcionar operaciones CRUD
 * estándar y consultas basadas en convenciones de Spring Data.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Recupera los roles cuyos identificadores estén incluidos en el conjunto dado.
     *
     * @param ids conjunto de identificadores de roles
     * @return lista de roles encontrados
     */
    List<Role> findByIdIn(Set<Long> ids);

    /**
     * Busca un rol por su nombre.
     *
     * @param name el nombre del rol a buscar.
     * @return un Optional que contiene el rol si se encuentra, o vacío si no existe.
     */
    Optional<Role> findByName(String name);

}
