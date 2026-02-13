package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.UserProfileFormDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.InvalidFileException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.FileStorageService;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/profile")
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
     * Muestra el formulario de edición del perfil del usuario.
     *
     * @param model  Modelo utilizado para enviar datos a la vista
     * @param locale Localización actual del usuario (idioma)
     * @return vista del formulario de perfil
     */
    @GetMapping("/edit")
    public String showProfileForm(Model model, Locale locale, Principal principal) {

        String email = principal.getName();
        logger.info("Mostrando formulario de perfil para el usuario fijo {}", email);

        try {
            UserProfileFormDTO formDTO = userProfileService.getFormByEmail(email);
            model.addAttribute("userProfileForm", formDTO);
            return "views/user-profile/user-profile-form";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el usuario para cargar el perfil: {}", ex.getMessage());
            String errorMessage = messageSource.getMessage(
                    "msg.user-controller.edit.notFound", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "views/user-profile/user-profile-form";

        } catch (Exception ex) {
            logger.error("Error inesperado cargando el formulario de perfil: {}", ex.getMessage());
            String errorMessage = messageSource.getMessage(
                    "msg.user-controller.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "views/user-profile/user-profile-form";
        }
    }

    /**
     * Procesa la actualización del perfil del usuario.
     * <p>
     * Valida los datos del formulario, actualiza la información del perfil
     * y gestiona la subida de la imagen de perfil si se proporciona.
     * </p>
     *
     * @param profileDto        DTO con los datos del formulario de perfil
     * @param result            Resultado de la validación
     * @param profileImageFile  Archivo de imagen de perfil (opcional)
     * @param redirectAttributes Atributos flash para mensajes tras redirección
     * @param locale            Localización actual del usuario
     * @return redirección al formulario de edición
     */
    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("userProfileForm") UserProfileFormDTO profileDto,
            BindingResult result,
            @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Principal principal) {

        String email = principal.getName();
        logger.info("Actualizando perfil para email={}", email);

        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de perfil para email={}", email);
            return "views/user-profile/user-profile-form";
        }

        try {
            userProfileService.updateProfile(email, profileDto, profileImageFile);
            String successMessage = messageSource.getMessage(
                    "msg.user-profile.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se pudo actualizar el perfil porque falta un recurso: {}",
                    ex.getMessage());
            String errorMessage = messageSource.getMessage(
                    "msg.user-profile.edit.notfound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

        } catch (InvalidFileException ex) {
            logger.warn("Imagen de perfil inválida: {}", ex.getMessage());
            String errorMessage = messageSource.getMessage(
                    "msg.user-profile.image.invalid", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

        } catch (Exception ex) {
            logger.error("Error al actualizar el perfil del usuario con id {}",
                    profileDto.getUserId(), ex);
            String errorMessage = messageSource.getMessage(
                    "msg.userProfile.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/profile/edit";
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
            UserProfileFormDTO profileDto,
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
