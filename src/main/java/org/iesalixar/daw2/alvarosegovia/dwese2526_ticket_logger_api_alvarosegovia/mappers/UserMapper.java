package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.mappers;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.*;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.Role;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.User;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.UserProfile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper utilitario entre la entidad {@link User} y sus DTOs.
 * Implementación simple sin frameworks de mapeo.
 */
public class UserMapper {


    // ─────────────────────────────────────────
    // Entity → DTO (listado/tabla básico)
    // ─────────────────────────────────────────


    /**
     * Convierte una entidad {@link User} a {@link UserDTO}.
     */
    public static UserDTO toDTO(User entity) {
        if (entity == null) return null;


        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setActive(entity.isActive());
        dto.setAccountNonLocked(entity.isAccountNonLocked());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.isEmailVerified());
        dto.setMustChangePassword(entity.isMustChangePassword());


        // ────────────────────────────────
        // Cargar roles si existen
        // ────────────────────────────────
        if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
            Set<String> roleNames = entity.getRoles().stream()
                    .map(Role::getName) // o Role::getDisplayName si prefieres
                    .collect(Collectors.toSet());
            dto.setRoles(roleNames);
        } else {
            dto.setRoles(new HashSet<>());
        }


        return dto;
    }


    /**
     * Convierte una lista de entidades {@link User} a una lista de {@link UserDTO}.
     */
    public static List<UserDTO> toDTOList(List<User> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(UserMapper::toDTO).toList();
    }


    // ─────────────────────────────────────────
    // Entity → DTO (detalle)
    // ─────────────────────────────────────────


    /**
     * Convierte una entidad {@link User} a {@link UserDetailDTO}.
     * Pensado para vistas de detalle donde, en el futuro, se puedan añadir
     * listas de roles, tickets, etc.
     */
    public static UserDetailDTO toDetailDTO(User entity) {
        if (entity == null) return null;


        UserDetailDTO dto = new UserDetailDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setActive(entity.isActive());
        dto.setAccountNonLocked(entity.isAccountNonLocked());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.isEmailVerified());
        dto.setMustChangePassword(entity.isMustChangePassword());


        // ────────────────────────────────
        // Cargar datos del perfil si existe
        // ────────────────────────────────
        UserProfile profile = entity.getProfile();


        if (profile != null) {
            dto.setFirstName(profile.getFirstName());
            dto.setLastName(profile.getLastName());
            dto.setPhoneNumber(profile.getPhoneNumber());
            dto.setProfileImage(profile.getProfileImage());
            dto.setBio(profile.getBio());
            dto.setLocale(profile.getLocale());
        }


        // ────────────────────────────────
        // Cargar roles si existen
        // ────────────────────────────────
        if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
            Set<String> roleNames = entity.getRoles().stream()
                    .map(Role::getName) // o Role::getDisplayName si prefieres el nombre legible
                    .collect(Collectors.toSet());
            dto.setRoles(roleNames);
        } else {
            dto.setRoles(new HashSet<>()); // para evitar nulls en la vista
        }




        return dto;
    }


    /**
     * Convierte una entidad {@link User} a {@link UserUpdateDTO}.
     * Útil cuando quieres precargar el formulario de edición.
     */
    public static UserUpdateDTO toUpdateDTO(User entity) {
        if (entity == null) return null;


        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setActive(entity.isActive());
        dto.setAccountNonLocked(entity.isAccountNonLocked());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.isEmailVerified());
        dto.setMustChangePassword(entity.isMustChangePassword());


        // ─────────────────────────────────────
        // Rellenar roleIds a partir de entity.roles
        // ─────────────────────────────────────
        if (entity.getRoles() != null) {
            Set<Long> roleIds = entity.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toSet());
            dto.setRoleIds(roleIds);
        }




        return dto;
    }


    // ─────────────────────────────────────────
    // DTO (create/update) → Entity
    // ─────────────────────────────────────────


    /**
     * Crea una nueva entidad {@link User} desde un {@link UserCreateDTO}.
     * El id se deja null para que se autogenere.
     */
    public static User toEntity(UserCreateDTO dto) {
        if (dto == null) return null;


        User e = new User();
        e.setEmail(dto.getEmail());
        e.setActive(dto.isActive());
        e.setAccountNonLocked(dto.isAccountNonLocked());
        e.setLastPasswordChange(dto.getLastPasswordChange());
        e.setPasswordExpiresAt(dto.getPasswordExpiresAt());
        e.setFailedLoginAttempts(dto.getFailedLoginAttempts());
        e.setEmailVerified(dto.isEmailVerified());
        e.setMustChangePassword(dto.isMustChangePassword());
        return e;
    }


    /**
     * Crea una nueva entidad {@link User} desde un {@link UserUpdateDTO}.
     * Útil si trabajas con update por reemplazo completo.
     * Si prefieres conservar relaciones u otros campos, carga antes la entidad
     * desde BD y usa {@link #copyToExistingEntity(UserUpdateDTO, User)}.
     */
    public static User toEntity(UserUpdateDTO dto) {
        if (dto == null) return null;


        User e = new User();
        e.setId(dto.getId());
        e.setEmail(dto.getEmail());
        e.setActive(dto.isActive());
        e.setAccountNonLocked(dto.isAccountNonLocked());
        e.setLastPasswordChange(dto.getLastPasswordChange());
        e.setPasswordExpiresAt(dto.getPasswordExpiresAt());
        e.setFailedLoginAttempts(dto.getFailedLoginAttempts());
        e.setEmailVerified(dto.isEmailVerified());
        e.setMustChangePassword(dto.isMustChangePassword());
        return e;
    }


    /**
     * Copia los campos editables de {@link UserUpdateDTO} sobre una entidad {@link User} existente.
     * Recomendado cuando quieres mantener el estado de persistencia y futuras relaciones
     * (por ejemplo roles, tickets, etc.).
     */
    public static void copyToExistingEntity(UserUpdateDTO dto, User entity) {
        if (dto == null || entity == null) return;


        entity.setEmail(dto.getEmail());
        entity.setActive(dto.isActive());
        entity.setAccountNonLocked(dto.isAccountNonLocked());
        entity.setLastPasswordChange(dto.getLastPasswordChange());
        entity.setPasswordExpiresAt(dto.getPasswordExpiresAt());
        entity.setFailedLoginAttempts(dto.getFailedLoginAttempts());
        entity.setEmailVerified(dto.isEmailVerified());
        entity.setMustChangePassword(dto.isMustChangePassword());
        // No tocar entity.setId(...)
        // Ni relaciones futuras como entity.getRoles(), etc.
    }


    /**
     * Crea una nueva entidad {@link User} desde un {@link UserCreateDTO}
     * y un conjunto de {@link Role} ya resueltos.
     *
     * Este método es útil cuando, desde el controlador/servicio,
     * ya has convertido los roleIds del DTO en entidades Role usando un DAO.
     */
    public static User toEntity(UserCreateDTO dto, Set<Role> roles) {
        if (dto == null) return null;

        User e = toEntity(dto); // reutilizamos la lógica existente
        e.setRoles(roles);
        return e;
    }


    /**
     * Crea una nueva entidad {@link User} desde un {@link UserUpdateDTO}
     * y un conjunto de {@link Role} ya resueltos.
     *
     * Útil si trabajas con update por reemplazo completo.
     */
    public static User toEntity(UserUpdateDTO dto, Set<Role> roles) {
        if (dto == null) return null;


        User e = toEntity(dto); // reutilizamos la lógica existente
        e.setRoles(roles);
        return e;
    }


    /**
     * Variante de copyToExistingEntity que también actualiza los roles.
     * Útil si prefieres el patrón "load entity + copy + save".
     */
    public static void copyToExistingEntity(UserUpdateDTO dto, User entity, Set<Role> roles) {
        if (dto == null || entity == null) return;


        copyToExistingEntity(dto, entity); // copia campos básicos
        entity.setRoles(roles);            // actualiza roles
    }
}
