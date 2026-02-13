package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar regiones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionUpdateDTO {

    @NotNull(message = "{msg.region.id.notEmpty}")
    private Long id;

    @NotBlank(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    private String code;

    @NotBlank(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.size}")
    private String name;
}
