package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    /**
     * Comprueba si existe alguna región con el código indicado.
     *
     * @param code código de la región
     * @return {@code true} si existe una región con ese código, {@code false} en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Comprueba si existe alguna región con el código indicado
     * excluyendo la región cuyo id se pasa como parámetro.
     * <p>
     * Se utiliza normalmente en operaciones de actualización
     * para evitar conflictos de unicidad con la propia entidad.
     *
     * @param code código de la región
     * @param id identificador de la región que se debe excluir de la comprobación
     * @return {@code true} si existe otra región con ese código, {@code false} en caso contrario
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Comprueba si existe alguna región con el nombre indicado.
     *
     * @param name nombre de la región
     * @return {@code true} si existe una región con ese nombre, {@code false} en caso contrario
     */
    boolean existsByName(String name);

    /**
     * Recupera una región por su identificador.
     *
     * @param id identificador de la región
     * @return un {@link Optional} que contiene la región si existe,
     *         o vacío si no se encuentra
     */
    @Override
    Optional<Region> findById(Long id);

    /**
     * Recupera una región por su identificador junto con sus provincias asociadas.
     * <p>
     * Utiliza una consulta JPQL con {@code LEFT JOIN FETCH} para cargar
     * de forma anticipada la colección de provincias y evitar problemas
     * de carga perezosa (LazyInitializationException).
     *
     * @param id identificador de la región
     * @return un {@link Optional} que contiene la región con sus provincias
     *         si existe, o vacío si no se encuentra
     */
    @Query("SELECT r FROM Region r LEFT JOIN FETCH r.provinces WHERE r.id = :id")
    Optional<Region> findByIdWithProvinces(@Param("id") Long id);

}
