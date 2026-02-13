package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Recupera el perfil asociado a un usuario dado su ID.
     *
     * @param userId identificador del usuario
     * @return Optional que contiene el UserProfile si existe, o vacío en caso contrario
     */
    @Query("SELECT up FROM UserProfile up WHERE up.user.id = :userId")
    Optional<UserProfile> findByUserId(@Param("userId") Long userId);

    /**
     * Comprueba si existe un perfil para el usuario indicado.
     *
     * @param userId identificador del usuario
     * @return true si existe un perfil para el usuario, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserProfile up WHERE up.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);

}
