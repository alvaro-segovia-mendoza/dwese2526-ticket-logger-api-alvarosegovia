package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import jakarta.transaction.Transactional;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionCreateDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionDetailDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.RegionUpdateDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.Region;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.mappers.RegionMapper;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Implementación de la lógica del negocio (casos de uso) para el CRUD de {@link Region}.
 * <p>
 *     Este se encarga de:
 *     <ul>
 *         <li>Interactuar con el repositorio para acceder a base de datos</li>
 *         <li>Aplicar reglas de negocio (por ejemplo, evitar códigos duplicados).</li>
 *         <li>Transformar entidades a DTOs y viceversa mediante {@link RegionMapper}</li>
 *         <li>Lanzar exepciones semánticas reutilizables {@link ResourceNotFoundException},
 *             {@link DuplicateResourceException}) para que la capa web (Controller MVC o REST)
 *             decida cómo presentarlas (mensaje flash, redirect, HTTP 404/409...).</li>
 *     </ul>
 * </p>
 */
@Service
@Transactional
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    /**
     * Obtiene un listado paginado de regiones.
     * <p>
     * Recupera las entidades {@link Region} desde el repositorio aplicando
     * paginación y ordenación, y las transforma en {@link RegionDTO}
     * para su uso en la capa de presentación.
     *
     * @param pageable objeto que encapsula la información de paginación
     *                 y ordenación (página, tamaño y criterio de orden)
     * @return una página de {@link RegionDTO} con las regiones solicitadas
     */
    @Override
    public Page<RegionDTO> list(Pageable pageable) {
        return regionRepository.findAll(pageable)
                .map(RegionMapper::toDTO);
    }

    /**
     * Obtiene los datos de una región para su edición.
     * <p>
     * Busca la región por su identificador y la convierte en un
     * {@link RegionUpdateDTO}. Si la región no existe, se lanza una
     * {@link ResourceNotFoundException}.
     *
     * @param id identificador único de la región
     * @return DTO con los datos de la región para edición
     * @throws ResourceNotFoundException si no existe una región con el ID indicado
     */
    @Override
    public RegionUpdateDTO getForEdit(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("region", "id", id)
                );

        return RegionMapper.toUpdateDTO(region);
    }

    /**
     * Crea una nueva región.
     * <p>
     * Comprueba previamente que no exista otra región con el mismo código.
     * Si el código ya está en uso, se lanza una {@link DuplicateResourceException}.
     * En caso contrario, se mapea el DTO a entidad y se persiste.
     *
     * @param dto DTO con los datos de la nueva región
     * @throws DuplicateResourceException si ya existe una región con el mismo código
     */
    @Override
    public RegionDTO create(RegionCreateDTO dto) {

        if (regionRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("region", "code", dto.getCode());
        }

        Region region = RegionMapper.toEntity(dto);
        region = regionRepository.save(region);
        return RegionMapper.toDTO(region);
    }

    /**
     * Actualiza una región existente.
     * <p>
     * Verifica que no exista otra región con el mismo código y distinto ID.
     * A continuación, recupera la región, copia los datos actualizados
     * y persiste los cambios.
     *
     * @param dto DTO con los datos actualizados de la región
     * @throws DuplicateResourceException si el código ya está en uso por otra región
     * @throws ResourceNotFoundException  si no existe la región a actualizar
     */
    @Override
    public RegionDTO update(RegionUpdateDTO dto) {

        if (regionRepository.existsByCodeAndIdNot(dto.getCode(), dto.getId())) {
            throw new DuplicateResourceException("region", "code", dto.getCode());
        }

        Region region = regionRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("region", "id", dto.getId())
                );

        RegionMapper.copyToExistingEntity(dto, region);
        region = regionRepository.save(region);
        return RegionMapper.toDTO(region);
    }

    /**
     * Elimina una región por su identificador.
     * <p>
     * Comprueba previamente que la región exista antes de proceder a su eliminación.
     * Si no existe, se lanza una {@link ResourceNotFoundException}.
     *
     * @param id identificador único de la región a eliminar
     * @throws ResourceNotFoundException si no existe una región con el ID indicado
     */
    @Override
    public void delete(Long id) {

        if (!regionRepository.existsById(id)) {
            throw new ResourceNotFoundException("region", "id", id);
        }

        regionRepository.deleteById(id);
    }

    /**
     * Obtiene el detalle completo de una región.
     * <p>
     * Recupera la región junto con sus provincias asociadas y la transforma
     * en un {@link RegionDetailDTO}. Si la región no existe, se lanza una
     * {@link ResourceNotFoundException}.
     *
     * @param id identificador único de la región
     * @return DTO con el detalle completo de la región
     * @throws ResourceNotFoundException si no existe la región solicitada
     */
    @Override
    public RegionDetailDTO getDetail(Long id) {

        Region region = regionRepository.findByIdWithProvinces(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("region", "id", id)
                );

        return RegionMapper.toDetailDTO(region);
    }

    @Override
    public List<RegionDTO> getAllRegions() {
        // Recuperamos todas las regiones desde la base de datos
        List<Region> regions = regionRepository.findAll();

        // Convertimos cada entidad a DTO usando el mapper
        return regions.stream()
                .map(RegionMapper::toDTO)
                .toList();
    }


}
