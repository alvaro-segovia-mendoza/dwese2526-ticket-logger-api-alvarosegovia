package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionCreateDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionDetailDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionUpdateDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Locale;

/**
 * Controlador que maneja las operaciones CRUD para la entidad 'Region'.
 * Utiliza 'RegionDAO' para interactuar con la base de datos.
 */
@RestController
@RequestMapping("/api/regions")
public class RegionController {

    // Logger para registrar eventos importantes en el Controller
    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionService regionService;

    @Autowired
    private MessageSource messageSource;


    /**
     * Lista paginada de regiones en JSON usando el Pageable estándar de Spring data.
     *
     * Ejemplos:
     *   GET /api/regions?page=0&size=10&sort=name,asc
     *   GET /api/regions?sort=name,desc
     */
    @GetMapping
    public ResponseEntity<Page<RegionDTO>> listRegions(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.info("Listando regiones (REST) page={}, size={}, sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        // Si aquí salta una excepción, la convertirá el @RestControllerAdvice a un HTTP status adecuado
        Page<RegionDTO> page = regionService.list(pageable);

        logger.info("Se han cargado {} regiones en la página {}.", page.getNumberOfElements(), page.getNumber());

        return ResponseEntity.ok(page);
    }


    /**
     * Devuelve el detalle de una región por ID (incluyendo provincias asociadas) en JSON.
     *
     * Ejemplo:
     *   GET /api/regions/10
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionDetailDTO> getRegionById(@PathVariable Long id) {

        logger.info("Retornando regione de id {}", id);

        // Si no existe, el service lanzará ResourceNotFoundException -> 404 (vía @RestControllerAdvice)
        RegionDetailDTO regionDTO = regionService.getDetail(id);

        return ResponseEntity.ok(regionDTO);
    }

    /**
     * Crea una nueva región.
     *
     * <p>
     *     Entrada: JSON (RegionCreateDTO). Salida: 201 Created + Location + RegionDTO.
     * </p>
     */
    @PostMapping
    public ResponseEntity<RegionDTO> createRegion(@Valid @RequestBody RegionCreateDTO dto) {
        logger.info("Creando region {}", dto);

        // 1) Delegamos la creación al servicio (incluye reglas de negocio: código único, etc.)
        RegionDTO created = regionService.create(dto);

        // 2) Construimos la cabecera Location con la URL del recurso recién creado: /api/regions/{id}
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        // 3) Respondemos con 201 Created + Location + body con el DTO creado
        return ResponseEntity.created(location).body(created);
    }
    /**
     *  Muestra el detalle de una región específica, incluyendo su lista de provincias asociadas.
     *
     * @param id                 Identificador único de la región que se desea consultar.
     * @param model              Modelo de Spring MVC utilizado para pasar datos a la vista.
     * @param redirectAttributes Objeto para enviar mensajes flash de error o de información al redirigir.
     * @param locale             Configuración regional actual del usuario (para internacionalización de mensajes).
     * @return El nombre de la plantilla thymeleaf que muestra el detalle de la región
     *          ({@code views/region/region-detail}), o una redirección a {@code /regions} en caso de error.
     */
    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Mostrando detalle de la región con ID {}", id);
        try {
            RegionDetailDTO regionDTO = regionService.getDetail(id);
            model.addAttribute("region", regionDTO);
            return "views/region/region-detail";
        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la región {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage(
                    "msg.region-controller.detail.error",
                    null,
                    locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        }
    }


    /**
     * Actualiza una región existente.
     * Entrada: JSON (RegionUpdateDTO). Salida: 200 OK + RegionDTO actualizada.
     *
     * Errores: (vía @RestControllerAdvice)
     * - 400 Bad request: validación DTO
     * - 404 Not Found: región no existe
     * - 409 Conflict: código duplicado
     * - 500 Internal Server Error: error inesperado
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegionDTO> updateRegion(@PathVariable Long id,
                                                  @Valid @RequestBody RegionUpdateDTO dto) {

        logger.info("Actualizando región con ID {}", id);

        // Buena práctica: asegurar consistencia entre path y body
        dto.setId(id);

        RegionDTO updated =  regionService.update(dto);

        logger.info("Región con ID {} actualizada con éxito.", id);

        return ResponseEntity.ok(updated);
    }


    /**
     * Elimina una región por ID.
     * <p>
     * REST: DELETE /api/regions/{id}
     * - 204 No content si se elimina correctamente
     * - 404 Not found si no existe (ResourceNotFoundException)
     * - 500 Internal Server Error si ocurre un error inesperado
     * </p>
     */
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        logger.info("Eliminando región con id: {}", id);

        // 1) Delegamos en el servicio:
        //      - si existe: elimina
        //      - si no existe: lanza ResourceNotFoundException (se convertirá a 404 en el @RestControllerAdvice)
        regionService.delete(id);

        logger.info("Región con ID {} eliminado con éxito.", id);

        // 2) En REST, lo habitual en un DELETE correcto es 204 No Content (sin body)
        return ResponseEntity.noContent().build();
    }
}
