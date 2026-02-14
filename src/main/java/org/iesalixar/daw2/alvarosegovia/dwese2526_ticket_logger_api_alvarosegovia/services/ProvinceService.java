package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProvinceService {

    Page<ProvinceDTO> list(Pageable pageable);

    ProvinceUpdateDTO getForEdit(Long id);

    ProvinceDTO create(ProvinceCreateDTO dto);

    ProvinceDTO update(ProvinceUpdateDTO dto);

    void delete(Long id);

    ProvinceDetailDTO getDetail(Long id);

    List<ProvinceDTO> getAllProvinces();
}
