package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.*;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Controlador que maneja las operaciones CRUD para la entidad 'User'.
 * Se apoya exclusivamente en {@link UserService}.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final long PASSWORD_EXPIRY_DAYS = 90;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Muestra el formulario para crear un nuevo usuario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("user", new UserCreateDTO());
        model.addAttribute("listRoles", userService.listRoles()); // roles para el select
        return "views/user/user-form";
    }

    /**
     * Muestra el formulario de edición de un usuario existente.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               Model model,
                               Locale locale,
                               RedirectAttributes redirectAttributes) {
        try {
            UserUpdateDTO userDTO = userService.getForEdit(id);
            model.addAttribute("user", userDTO);
            model.addAttribute("listRoles", userService.listRoles());
        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el usuario con ID {}", id);
            String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        } catch (Exception e) {
            logger.error("Error al cargar el usuario {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.user-controller.edit.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        }
        return "views/user/user-form";
    }

    /**
     * Muestra el detalle de un usuario.
     */
    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        try {
            UserDetailDTO userDTO = userService.getDetail(id);
            model.addAttribute("user", userDTO);
            return "views/user/user-detail";
        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el usuario con ID {}", id);
            String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        } catch (Exception e) {
            logger.error("Error al obtener el detalle del usuario {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.user-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        }
    }

    /**
     * Lista todos los usuarios paginados.
     */
    @GetMapping
    public String listUsers(@PageableDefault(size = 10, sort = "email", direction = Sort.Direction.ASC) Pageable pageable,
                            Model model) {
        try {
            Page<UserDTO> users = userService.list(pageable);
            model.addAttribute("page", users);

            String sortParam = "email,asc";
            if (users.getSort().isSorted()) {
                Sort.Order order = users.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar los usuarios: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al listar los usuarios.");
        }
        return "views/user/user-list";
    }

    /**
     * Inserta un nuevo usuario.
     */
    @PostMapping("/insert")
    public String insertUser(@Valid @ModelAttribute("user") UserCreateDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {

        if (result.hasErrors()) {
            return "views/user/user-form";
        }

        try {
            userService.create(userDTO);
            logger.info("Usuario {} insertado con éxito.", userDTO.getEmail());
            return "redirect:/users";
        } catch (DuplicateResourceException ex) {
            logger.warn("Email duplicado: {}", userDTO.getEmail());
            String msg = messageSource.getMessage("msg.user-controller.insert.emailExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users/new";
        } catch (Exception e) {
            logger.error("Error al insertar el usuario {}: {}", userDTO.getEmail(), e.getMessage(), e);
            String msg = messageSource.getMessage("msg.user-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users/new";
        }
    }

    /**
     * Actualiza un usuario existente.
     */
    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") UserUpdateDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {

        if (result.hasErrors()) {
            return "views/user/user-form";
        }

        try {
            userService.update(userDTO);
            logger.info("Usuario con ID {} actualizado con éxito.", userDTO.getId());
            return "redirect:/users";
        } catch (DuplicateResourceException ex) {
            logger.warn("Email duplicado: {}", userDTO.getEmail());
            String msg = messageSource.getMessage("msg.user-controller.update.emailExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users/edit?id=" + userDTO.getId();
        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        } catch (Exception e) {
            String msg = messageSource.getMessage("msg.user-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users/edit?id=" + userDTO.getId();
        }
    }

    /**
     * Elimina un usuario.
     */
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        try {
            userService.delete(id);
            logger.info("Usuario con ID {} eliminado con éxito.", id);
        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
        } catch (Exception e) {
            String msg = messageSource.getMessage("msg.user-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
        }
        return "redirect:/users";
    }
}
