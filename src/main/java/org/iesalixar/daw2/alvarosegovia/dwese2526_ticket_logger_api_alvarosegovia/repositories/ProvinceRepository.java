package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

    /**
     * Comprueba si existe alguna provincia con el código indicado.
     *
     * @param code código de la provincia
     * @return {@code true} si existe una provincia con ese código,
     *         {@code false} en caso contrario
     */
    boolean existsByCode(String code);

    /**
     * Comprueba si existe alguna provincia con el código indicado
     * excluyendo la provincia cuyo id se pasa como parámetro.
     * <p>
     * Se utiliza normalmente en operaciones de actualización
     * para evitar conflictos de unicidad con la propia entidad.
     *
     * @param code código de la provincia
     * @param id identificador de la provincia que se debe excluir
     * @return {@code true} si existe otra provincia con ese código,
     *         {@code false} en caso contrario
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Comprueba si existe alguna provincia con el nombre indicado.
     *
     * @param name nombre de la provincia
     * @return {@code true} si existe una provincia con ese nombre,
     *         {@code false} en caso contrario
     */
    boolean existsByName(String name);

    /**
     * Recupera una provincia por su identificador.
     *
     * @param id identificador de la provincia
     * @return un {@link Optional} que contiene la provincia si existe,
     *         o vacío si no se encuentra
     */
    @Override
    Optional<Province> findById(Long id);

    /**
     * Recupera una provincia por su identificador junto con su región asociada.
     * <p>
     * Utiliza una consulta JPQL con {@code LEFT JOIN FETCH} para cargar
     * de forma anticipada la relación con la región y evitar problemas
     * de carga perezosa (LazyInitializationException).
     *
     * @param id identificador de la provincia
     * @return un {@link Optional} que contiene la provincia con su región
     *         si existe, o vacío si no se encuentra
     */
    @Query("SELECT p FROM Province p LEFT JOIN FETCH p.region WHERE p.id = :id")
    Optional<Province> findByIdWithRegion(@Param("id") Long id);

}
