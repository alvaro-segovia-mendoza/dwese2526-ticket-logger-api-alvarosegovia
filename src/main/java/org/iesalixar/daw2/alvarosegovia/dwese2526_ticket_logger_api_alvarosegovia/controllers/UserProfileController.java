package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.UserProfileDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.UserProfilePatchDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.InvalidFileException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.FileStorageService;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Locale;

/**
 * Controlador MVC encargado de la gestión del perfil de usuario.
 * <p>
 * Permite visualizar y actualizar los datos del perfil, incluyendo
 * la subida y gestión de la imagen de perfil.
 * </p>
 *
 * Ruta base: /profile
 */
@Controller
@RequestMapping("/api/profile")
@Validated
public class UserProfileController {

    /** Logger para trazas y depuración */
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    /** Servicio de internacionalización de mensajes */
    @Autowired
    private MessageSource messageSource;

    /** Servicio de lógica de negocio para el perfil de usuario */
    @Autowired
    private UserProfileService userProfileService;

    /** Servicio encargado del almacenamiento de archivos */
    @Autowired
    private FileStorageService fileStorageService;


    /**
     * GET API: devuelve el perfil para el usuario autenticado.
     * Equivale a "mostrar formulario", pero en API devuelve datos.
     */
    @GetMapping
    public ResponseEntity<UserProfileDTO> getMyProfile(Principal principal) {
        String email = principal.getName();
        logger.info("getMyProfile email: {}", email);

        UserProfileDTO dto = userProfileService.getFormByEmail(email);
        return ResponseEntity.ok(dto);
    }

    /**
     * Actualiza el perfil del usuario autenticado (PATCH).
     *
     * <p>Consume <b>multipart/form-data</b> con:</p>
     * <ul>
     *     <li><b>profile</b>: JSON con los campos a modificar (solo se actualizan los presentes).</li>
     *     <li><b>profileImageFile</b> (opcional): nueva imagen de perfil.</li>
     * </ul>
     *
     * <p>El usuario se identifica a partir del {@link Principal}.</p>
     *
     * @param patchDTO datos parciales del perfil a aplicar
     * @param profileImageFile imagen de perfil opcional
     * @param principal usuario autenticado
     * @return perfil actualizado
     */
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileDTO> patchMyProfile(
            @ModelAttribute UserProfilePatchDTO patchDTO,
            @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile,
            Principal principal
    ) {
        String email = principal.getName();
        logger.info("patchMyProfile email: {}", email);

        userProfileService.updateProfile(email, patchDTO, profileImageFile);

        UserProfileDTO dto = userProfileService.getFormByEmail(email);
        return ResponseEntity.ok(dto);
    }

    // ────────────── MÉTODOS PRIVADOS AUXILIARES ──────────────

    /**
     * Gestiona la validación, almacenamiento y sustitución de la imagen
     * de perfil del usuario.
     *
     * @param profileDto DTO del perfil de usuario
     * @param file Archivo de imagen subido
     * @param redirectAttributes Atributos flash para mensajes de error
     * @param locale Localización actual del usuario
     */
    private void handleProfileImage(
            UserProfileDTO profileDto,
            MultipartFile file,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (file == null || file.isEmpty()) return;

        logger.info("Se ha subido un nuevo archivo de imagen para el perfil del usuario {}",
                profileDto.getUserId());

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            logger.warn("Archivo de tipo no permitido: {}", contentType);
            String msg = messageSource.getMessage(
                    "msg.userProfile.image.invalidType", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            throw new RuntimeException("Tipo de archivo no válido");
        }

        long maxSizeBytes = 2 * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            logger.warn("Archivo demasiado grande");
            String msg = messageSource.getMessage(
                    "msg.userProfile.image.tooLarge", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            throw new RuntimeException("Archivo demasiado grande");
        }

        String oldImagePath = profileDto.getProfileImage();
        String newImageWebPath = fileStorageService.saveFile(file);

        if (newImageWebPath == null) {
            logger.error("No se pudo guardar la nueva imagen de perfil para el usuario {}",
                    profileDto.getUserId());
            String msg = messageSource.getMessage(
                    "msg.userProfile.image.saveError", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            throw new RuntimeException("Error guardando imagen");
        }

        profileDto.setProfileImage(newImageWebPath);

        if (oldImagePath != null && !oldImagePath.isBlank()) {
            logger.info("Eliminando imagen anterior de perfil: {}", oldImagePath);
            fileStorageService.deleteFile(oldImagePath);
        }
    }
}
