package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.*;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.ProvinceService;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Controlador que maneja las operaciones CRUD para la entidad 'Province'.
 * Se apoya en los servicios {@link ProvinceService} y {@link RegionService}.
 */
@RestController
@RequestMapping("/provinces")
public class ProvinceController {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private MessageSource messageSource;



    /**
     * Muestra el detalle de una provincia específica.
     */
    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        try {
            ProvinceDetailDTO provinceDTO = provinceService.getDetail(id);
            model.addAttribute("province", provinceDTO);
            return "views/province/province-detail";
        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la provincia {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.province-controller.detail.error", null, locale));
            return "redirect:/provinces";
        }
    }

    /**
     * Lista todas las provincias paginadas.
     */
    @GetMapping
    public String listProvinces(@PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                Model model, Locale locale) {
        try {
            Page<ProvinceDTO> provinces = provinceService.list(pageable);
            model.addAttribute("page", provinces);

            String sortParam = "name,asc";
            if (provinces.getSort().isSorted()) {
                Sort.Order order = provinces.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar las provincias: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al listar las provincias.");
        }
        return "views/province/province-list";
    }

    /**
     * Procesa la inserción de una nueva provincia.
     */
    @PostMapping("/insert")
    public String insertProvince(@Valid @ModelAttribute("province") ProvinceCreateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model, Locale locale) {
        logger.info("Insertando nueva provincia con código {}", provinceDTO.getCode());

        if (result.hasErrors()) {
            return "views/province/province-form";
        }

        try {
            provinceService.create(provinceDTO);
            logger.info("Provincia {} insertada con éxito.", provinceDTO.getCode());
            return "redirect:/provinces";
        } catch (DuplicateResourceException ex) {
            String errorMessage = messageSource.getMessage(
                    "msg.province-controller.insert.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/new";
        } catch (Exception e) {
            String errorMessage = messageSource.getMessage(
                    "msg.province-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/new";
        }
    }

    /**
     * Procesa la actualización de una provincia existente.
     */
    @PostMapping("/update")
    public String updateProvince(@Valid @ModelAttribute("province") ProvinceUpdateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model, Locale locale) {
        if (result.hasErrors()) {
            return "views/province/province-form";
        }

        try {
            provinceService.update(provinceDTO);
            logger.info("Provincia con ID {} actualizada con éxito.", provinceDTO.getId());
            return "redirect:/provinces";
        } catch (DuplicateResourceException ex) {
            String errorMessage = messageSource.getMessage(
                    "msg.province-controller.update.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/edit?id=" + provinceDTO.getId();
        } catch (ResourceNotFoundException ex) {
            String notFound = messageSource.getMessage(
                    "msg.province-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/provinces";
        } catch (Exception e) {
            String errorMessage = messageSource.getMessage(
                    "msg.province-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/edit?id=" + provinceDTO.getId();
        }
    }

    /**
     * Procesa la eliminación de una provincia.
     */
    @PostMapping("/delete")
    public String deleteProvince(@RequestParam("id") Long id,
                                 RedirectAttributes redirectAttributes,
                                 Locale locale) {
        logger.info("Eliminando provincia con ID {}", id);

        try {
            provinceService.delete(id);
            return "redirect:/provinces";
        } catch (ResourceNotFoundException ex) {
            String notFound = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/provinces";
        } catch (Exception e) {
            String errorMessage = messageSource.getMessage("msg.province-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces";
        }
    }

}
