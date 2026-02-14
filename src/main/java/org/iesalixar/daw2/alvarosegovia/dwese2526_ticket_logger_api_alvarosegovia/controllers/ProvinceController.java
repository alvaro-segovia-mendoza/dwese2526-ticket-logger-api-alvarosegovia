package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.*;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.ProvinceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST que maneja las operaciones CRUD para la entidad 'Province'.
 */
@RestController
@RequestMapping("/api/provinces")
public class ProvinceController {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private ProvinceService provinceService;

    /**
     * Lista paginada de provincias en JSON.
     */
    @GetMapping
    public ResponseEntity<Page<ProvinceDTO>> getProvinces(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.info("Listando provincias (REST) page={}, size={}, sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<ProvinceDTO> page = provinceService.list(pageable);

        logger.info("Se han cargado {} provincias en la página {}.", page.getNumberOfElements(), page.getNumber());

        return ResponseEntity.ok(page);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProvinceDTO>> getAllRegions() {
        logger.info("Solicitando la lista de todas las provincias...");
        List<ProvinceDTO> provinces = provinceService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }
    /**
     * Devuelve el detalle de una provincia por ID en JSON.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProvinceDetailDTO> getProvinceById(@PathVariable Long id) {

        logger.info("Retornando provincia de id {}", id);

        ProvinceDetailDTO provinceDTO = provinceService.getDetail(id);

        return ResponseEntity.ok(provinceDTO);
    }

    /**
     * Crea una nueva provincia.
     */
    @PostMapping
    public ResponseEntity<ProvinceDTO> createProvince(@Valid @RequestBody ProvinceCreateDTO dto) {

        logger.info("Creando provincia {}", dto);

        ProvinceDTO created = provinceService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * Actualiza una provincia existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProvinceDTO> updateProvince(@PathVariable Long id,
                                                      @Valid @RequestBody ProvinceUpdateDTO dto) {

        logger.info("Actualizando provincia con ID {}", id);

        dto.setId(id);

        ProvinceDTO updated = provinceService.update(dto);

        logger.info("Provincia con ID {} actualizada con éxito.", id);

        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina una provincia por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvince(@PathVariable Long id) {

        logger.info("Eliminando provincia con id: {}", id);

        provinceService.delete(id);

        logger.info("Provincia con ID {} eliminada con éxito.", id);

        return ResponseEntity.noContent().build();
    }
}
