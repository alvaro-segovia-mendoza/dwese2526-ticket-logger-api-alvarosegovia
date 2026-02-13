package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProvinceService {

    Page<ProvinceDTO> list(Pageable pageable);

    ProvinceUpdateDTO getForEdit(Long id);

    void create(ProvinceCreateDTO dto);

    void update(ProvinceUpdateDTO dto);

    void delete(Long id);

    ProvinceDetailDTO getDetail(Long id);

}
