package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.mappers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.UserProfileDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.User;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.UserProfile;

/**
 * Mapper utilitario entre la entidad {@link UserProfile} y su DTO de formulario
 * {@link UserProfileDTO}.
 *
 * Está pensado para la funcionalidad "MI PERFIL", donde el mismo formulario
 * se usa tanto para crear el perfil (si no existe) como para editarlo.
 *
 * Implementación simple sin frameworks de mapeo.
 */
public class UserProfileMapper {

    // -----------------------------------
    // Entity -> DTO (formulario perfil)
    // -----------------------------------

    /**
     * Convierte una combinación de {@link User} + {@link UserProfile} en un
     * {@link UserProfileDTO}.
     *
     * Si el perfil es null, se devuelve un DTO con datos básicos del User
     * (id, email) y el resto de campos vacíos, útil para mostrar el formulario
     * de creación.
     *
     * @param user      User autenticado (obligatorio).
     * @param profile   Perfil de usuario (puede ser null).
     * @return DTO para el formulario de perfil.
     */
    public static UserProfileDTO toFormDto(User user, UserProfile profile) {
        if (user == null) {
            return null;
        }

        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getId());
        dto.setEmail(user.getEmail());

        if (profile != null) {
            dto.setFirstName(profile.getFirstName());
            dto.setLastName(profile.getLastName());
            dto.setPhoneNumber(profile.getPhoneNumber());
            dto.setProfileImage(profile.getProfileImage());
            dto.setBio(profile.getBio());
            dto.setLocale(profile.getLocale());
        }

        return dto;
    }

    // -----------------------------------
    // DTO (form) -> Entity (create/update)
    // -----------------------------------

    /**
     * Crea una nueva entidad {@link UserProfile} a partir de un
     * {@link UserProfileDTO} y un {@link User}.
     *
     * Pensado para el caso en el que el perfil aún no existe en base de datos.
     * El id toma del User asociado mediante la anotación @MapsId.
     *
     * @param dto       DTO del formulario.
     * @param user      Entidad User asociado (obligatorio).
     * @return Nueva entidad UserProfile sin persistir.
     */
    public static UserProfile toNewEntity(UserProfileDTO dto, User user) {
        if (dto == null || user == null) {
            return null;
        }

        UserProfile profile = new UserProfile();
        // Relación 1:1 con shared primary key
        profile.setUser(user);

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setProfileImage(dto.getProfileImage());
        profile.setBio(dto.getBio());
        profile.setLocale(dto.getLocale());

        return profile;
    }

    /**
     * Copia los campos editables {@link UserProfileDTO} sobre una
     * entidad {@link UserProfile} existente.
     *
     * Recomendado para el caso de edición, manteniendo el estado de persistencia
     * y las propiedades gestionadas por la BD (created_at, updated_at, etc.).
     *
     * @param dto       DTO con los datos del formulario.
     * @param profile   Entidad UserProfile existente (ya cargado de BD).
     */
    public static void copyToExistingEntity(@Valid UserProfileDTO dto, UserProfile profile) {
        if (dto == null || profile == null) {
            return;
        }

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setProfileImage(dto.getProfileImage());
        profile.setBio(dto.getBio());
        profile.setLocale(dto.getLocale());
    }

}
