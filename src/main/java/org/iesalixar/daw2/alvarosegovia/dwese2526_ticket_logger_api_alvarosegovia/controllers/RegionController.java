package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.util.List;
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
    @Operation(summary = "Lista paginada de regiones", description = "Devuelve una página de regiones en formato JSON")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de regiones devuelta correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<RegionDTO>> getRegions(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.info("Listando regiones (REST) page={}, size={}, sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<RegionDTO> page = regionService.list(pageable);

        logger.info("Se han cargado {} regiones en la página {}.", page.getNumberOfElements(), page.getNumber());

        return ResponseEntity.ok(page);
    }

     @GetMapping("/all")
     public ResponseEntity<List<RegionDTO>> getAllRegions() {
        logger.info("Solicitando la lista de todas las regiones...");
        List<RegionDTO> regions = regionService.getAllRegions();
        return ResponseEntity.ok(regions);
     }


    /**
     * Devuelve el detalle de una región por ID (incluyendo provincias asociadas) en JSON.
     *
     * Ejemplo:
     *   GET /api/regions/10
     */
    @Operation(summary = "Detalle de una región por ID", description = "Incluye provincias asociadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región encontrada"),
            @ApiResponse(responseCode = "404", description = "Región no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RegionDetailDTO> getRegionById(@PathVariable Long id) {
        logger.info("Retornando region de id {}", id);
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
    @Operation(summary = "Crea una nueva región", description = "Entrada JSON: RegionCreateDTO. Salida: 201 Created + Location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Región creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la creación"),
            @ApiResponse(responseCode = "409", description = "Código de región duplicado")
    })
    @PostMapping
    public ResponseEntity<RegionDTO> createRegion(@Valid @RequestBody RegionCreateDTO dto) {
        logger.info("Creando region {}", dto);

        RegionDTO created = regionService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
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
    @Operation(
            summary = "Actualiza una región por ID",
            description = "Actualiza los datos de una región existente. Si no existe, devuelve 404 Not Found."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Región actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Región no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado para actualizar la región")
    })
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
    @Operation(
            summary = "Elimina una región por ID",
            description = "Si la región existe, se elimina. Si no existe, devuelve 404 Not Found."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Región eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Región no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado para eliminar la región")
    })
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
