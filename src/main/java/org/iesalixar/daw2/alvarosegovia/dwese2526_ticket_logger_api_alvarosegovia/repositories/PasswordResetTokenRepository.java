package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para gestionar tokens de recuperación de contraseña.
 * <p>
 * Permite localizar tokens por su hash, invalidar tokens activos de un usuario y limpiar tokens antiguos.
 * </p>
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {


    /**
     * Busca un token de recuperación por su hash (SHA-256 en hex).
     *
     * @param tokenHash hash del token recibido (nunca el token en claro).
     * @return token si existe; vacío si no se encuentra.
     */
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);


    /**
     * Invalida (marca como usados) todos los tokens activos de un usuario.
     * <p>
     * Se usa típicamente al generar un nuevo token para asegurar que solo haya uno válido.
     * </p>
     *
     * @param userId id del usuario.
     * @param now    fecha/hora actual para registrar el momento de invalidación.
     * @return número de tokens afectados.
     */
    @Modifying
    @Query("update PasswordResetToken t set t.usedAt = :now where t.user.id = :userId and t.usedAt is null")
    int invalidateAllActiveTokensForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);


    /**
     * Elimina tokens antiguos (caducados) o ya consumidos.
     * <p>
     * Útil para tareas de mantenimiento (limpieza) y para reducir el crecimiento de la tabla.
     * </p>
     *
     * @param now fecha/hora actual para comparar caducidades.
     * @return número de tokens eliminados.
     */
    @Modifying
    @Query("delete from PasswordResetToken t where t.expiresAt < :now or t.usedAt is not null")
    int deleteOldTokens(@Param("now") LocalDateTime now);
}
