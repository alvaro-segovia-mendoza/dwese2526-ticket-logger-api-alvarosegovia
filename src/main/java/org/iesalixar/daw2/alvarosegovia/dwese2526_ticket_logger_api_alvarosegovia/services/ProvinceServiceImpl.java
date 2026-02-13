package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.ProvinceCreateDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.ProvinceDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.ProvinceDetailDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.ProvinceUpdateDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.Province;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.mappers.ProvinceMapper;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de gestión de provincias.
 * <p>
 * Proporciona la lógica de negocio necesaria para la creación,
 * consulta, actualización y eliminación de provincias, así como
 * la obtención de distintos DTOs según el caso de uso.
 * </p>
 */
@Service
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

    /** Repositorio de acceso a datos de provincias */
    @Autowired
    private ProvinceRepository provinceRepository;

    /**
     * Obtiene una lista paginada de provincias.
     *
     * @param pageable información de paginación y ordenación
     * @return página de provincias en formato DTO
     */
    @Override
    public Page<ProvinceDTO> list(Pageable pageable) {
        return provinceRepository.findAll(pageable)
                .map(ProvinceMapper::toDTO);
    }

    /**
     * Obtiene los datos de una provincia para su edición.
     *
     * @param id identificador de la provincia
     * @return DTO con los datos editables de la provincia
     * @throws ResourceNotFoundException si la provincia no existe
     */
    @Override
    public ProvinceUpdateDTO getForEdit(Long id) {
        Province province = provinceRepository.findByIdWithRegion(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("province", "id", id)
                );

        return ProvinceMapper.toUpdateDTO(province);
    }


    /**
     * Crea una nueva provincia.
     * <p>
     * Antes de la creación se comprueba que no exista otra provincia
     * con el mismo nombre.
     * </p>
     *
     * @param dto DTO con los datos de creación
     * @throws DuplicateResourceException si el nombre ya existe
     */
    @Override
    public void create(ProvinceCreateDTO dto) {

        if (provinceRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "province", "name", dto.getName());
        }

        Province province = ProvinceMapper.toEntity(dto);
        provinceRepository.save(province);
    }

    /**
     * Actualiza una provincia existente.
     * <p>
     * Se valida que el código de la provincia no esté duplicado
     * en otra entidad distinta.
     * </p>
     *
     * @param dto DTO con los datos actualizados
     * @throws DuplicateResourceException si el código ya existe
     * @throws ResourceNotFoundException si la provincia no existe
     */
    @Override
    public void update(ProvinceUpdateDTO dto) {

        if (provinceRepository.existsByCodeAndIdNot(
                dto.getCode(), dto.getId())) {
            throw new DuplicateResourceException(
                    "province", "code", dto.getCode());
        }

        Province province = provinceRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "province", "id", dto.getId())
                );

        ProvinceMapper.copyToExistingEntity(dto, province);
        provinceRepository.save(province);
    }

    /**
     * Elimina una provincia por su identificador.
     *
     * @param id identificador de la provincia
     * @throws ResourceNotFoundException si la provincia no existe
     */
    @Override
    public void delete(Long id) {

        if (!provinceRepository.existsById(id)) {
            throw new ResourceNotFoundException("province", "id", id);
        }

        provinceRepository.deleteById(id);
    }

    /**
     * Obtiene el detalle completo de una provincia, incluyendo su región.
     *
     * @param id identificador de la provincia
     * @return DTO con la información detallada de la provincia
     * @throws ResourceNotFoundException si la provincia no existe
     */
    @Override
    public ProvinceDetailDTO getDetail(Long id) {

        Province province = provinceRepository.findByIdWithRegion(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("province", "id", id)
                );

        return ProvinceMapper.toDetailDTO(province);
    }
}
