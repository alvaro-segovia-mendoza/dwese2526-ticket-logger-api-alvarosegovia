package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO genérico de lectura para Province.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO {
    private Long id;
    private String code;
    private String name;
    private String regionName;

}
