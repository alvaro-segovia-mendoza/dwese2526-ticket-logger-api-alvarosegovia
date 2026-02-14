package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.mappers;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.*;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.Province;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.Region;

import javax.swing.*;
import java.util.List;

/**
 * Mapper utilizado entre entidades (Region/Province) y sus DTOs.
 * Implementación simple sin frameworks de mapeo.
 */
public class RegionMapper {

    // --------------------------------------
    // Entity -> DTO (listado/tabla básico)
    // --------------------------------------
    /**
     * Convierte una entidad {@link Region} a {@link RegionDTO}.
     */
    public static RegionDTO toDTO(Region entity) {
        if (entity == null) return null;
        RegionDTO dto = new RegionDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        return dto;
    }

    /**
     * Convierte una lista de entidades {@link Region} a {@link RegionDTO}.
     */
    public static List<RegionDTO> toDTOList(List<Region> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(RegionMapper::toDTO).toList();
    }

    // -----------------------------------------
    // Entity -> DTO (detalle con provincias)
    // -----------------------------------------
    /**
     * Convierte un {@link Region} a {@link RegionDetailDTO}, mapeando su lista de provincias
     */
    public static RegionDetailDTO toDetailDTO(Region entity) {
        if (entity == null) return null;

        RegionDetailDTO dto = new RegionDetailDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setProvinces(toProvinceList(entity.getProvinces()));
        return dto;
    }

    /**
     * Convierte una entidad {@link Province} a {@link ProvinceDTO}.
     */
    public static ProvinceDTO toProvinceDTO(Province province) {
        if (province == null) return null;

        ProvinceDTO dto = new ProvinceDTO();
        dto.setId(province.getId());
        dto.setCode(province.getCode());
        dto.setName(province.getName());
        dto.setRegionName(province.getRegion() != null ? province.getRegion().getName() : null);
        return dto;
    }


    /**
     * Convierte una lista de {@link Province} a {@link ProvinceDTO}.
     */
    public static List<ProvinceDTO> toProvinceList(List<Province> provinces) {
        if (provinces == null) return List.of();
        return provinces.stream().map(RegionMapper::toProvinceDTO).toList();
    }

    public static RegionUpdateDTO toUpdateDTO(Region entity) {
        if (entity == null) return null;
        RegionUpdateDTO dto = new RegionUpdateDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        return dto;
    }

    // ----------------------------------------------------
    // DTO (create/update) -> Entity
    // ----------------------------------------------------
    /**
     * Crea una nueva entidad {@link Region} desde un {@link RegionCreateDTO}.
     * (id se deja null para autogenerarse)
     */
    public static Region toEntity(RegionCreateDTO dto) {
        if (dto == null) return null;
        Region e = new Region();
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        return e;
    }

    /**
     * Crea una entidad {@link Region} desde un {@link RegionUpdateDTO}.
     * Útil si trabajas con update por reemplazo.
     * Si prefieres evitar perder relaciones, carga la entidad de la BD y copia campos a mano.
     */
    public Region toEntity(RegionUpdateDTO dto) {
        if (dto == null) return null;
        Region e = new Region();
        e.setId(dto.getId());
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        return e;
    }

    /**
     * Copia los datos de un DTO de actualización a una entidad existente.
     * <p>
     * Este método permite actualizar los campos modificables de una entidad
     * {@link Region} a partir de un {@link RegionUpdateDTO}, sin alterar
     * la identidad de la entidad ni sus relaciones asociadas.
     * </p>
     *
     * @param dto    el DTO que contiene los nuevos valores para la entidad
     * @param entity la entidad existente que se va a actualizar
     */
    public static void copyToExistingEntity(RegionUpdateDTO dto, Region entity) {
        if (dto == null || entity == null) return;
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        // NO tocar entity.setId(...)
        // Tampoco tocar relaciones como entity.getProvince() aquí.
    }

}
