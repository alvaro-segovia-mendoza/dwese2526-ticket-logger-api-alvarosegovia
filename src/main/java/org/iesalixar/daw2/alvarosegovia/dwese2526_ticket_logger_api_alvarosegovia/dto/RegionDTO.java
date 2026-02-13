package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para la entidad Region.
 * Se utiliza para transferir información de regions entre capas,
 * sin exponer directamente la entidad ni datos sensibles innecesarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionDTO {
    private Long id;
    private String code;
    private String name;
}
