package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionCreateDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionDetailDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegionService {

    Page<RegionDTO> list(Pageable pageable);

    RegionUpdateDTO getForEdit(Long id);

    RegionDTO create(RegionCreateDTO dto);

    RegionDTO update(RegionUpdateDTO dto);

    void delete(Long id);

    RegionDetailDTO getDetail(Long id);

}
