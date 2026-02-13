package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.*;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<UserDTO> list(Pageable pageable);

    UserUpdateDTO getForEdit(Long id);

    void create(UserCreateDTO dto);

    void update(UserUpdateDTO dto);

    void delete(Long id);

    UserDetailDTO getDetail(Long id);

    List<Role> listRoles();

}
