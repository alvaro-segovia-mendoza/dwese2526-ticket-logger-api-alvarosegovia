package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de detalle para Provincias.
 * Incluye la región a la que pertenece para poder navegar desde el detalle.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDetailDTO {
    private Long id;
    private String code;
    private String name;

    /** Región asociado a la provincia (objeto embebido para el detalle). */
    private RegionDTO region;
}
