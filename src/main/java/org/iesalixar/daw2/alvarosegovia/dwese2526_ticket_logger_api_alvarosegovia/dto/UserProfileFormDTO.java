package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el formulario de edición del perfil de usuario.
 *
 * Se utiliza tanto para crear el perfil (si no existe)
 * como para actualizarlo (si ya existe).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileFormDTO {

    /** ID del usuario autenticado (se usa para buscar/crear el perfil) */
    private Long userId;

    /** Email solo lectura, informativo en la vista */
    private String email;

    /** VARCHAR(60) NOT NULL */
    @NotBlank(message = "{msg.userProfile.firstName.notblank}")
    @Size(max = 60, message = "{msg.userProfile.firstName.size}")
    private String firstName;

    /** VARCHAR(80) NOT NULL */
    @NotBlank(message = "{msg.userProfile.lastName.notblank}")
    @Size(max = 80, message = "{msg.userProfile.lastName.size}")
    private String lastName;

    /** VARCHAR(30) NULL */
    @Size(max = 30, message = "{msg.userProfile.phoneNumber.size}")
    private String phoneNumber;

    /** VARCHAR(255) NULL - Ruta/URL de la imagen del perfil */
    @Size(max = 255, message = "{msg.userProfile.profileImage.size}")
    private String profileImage;

    /** VARCHAR(500) NULL - Pequeña descripción / biografía */
    @Size(max = 500, message = "{msg.userProfile.bio.size}")
    private String bio;

    /** VARCHAR(10) NULL - Código idioma/locale (es_ES, en_ES...) */
    @Size(max = 10, message = "{msg.userProfile.locale.size}")
    private String locale;

}
