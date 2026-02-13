package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import jakarta.transaction.Transactional;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.UserProfileFormDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.User;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.UserProfile;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.InvalidFileException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.mappers.UserProfileMapper;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories.UserProfileRepository;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private static final long MAX_IMAGE_SIZE_BYTES = 2 * 1024 * 1024;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;


    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public UserProfileFormDTO getFormByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user", "email", email));

        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(user.getId());
        UserProfile profile = profileOpt.orElse(null);
        return UserProfileMapper.toFormDto(user, profile);
    }

    @Override
    public void updateProfile(String email, UserProfileFormDTO profileDTO, MultipartFile profileImageFile) {

        logger.info("Actualizando perfil para email={}", email);

        // 1) Comprobar que existe el usuario
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user", "email", email));

        Long userId = user.getId();
        // 2) Cargar perfil (puede no existir)
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        boolean isNew = (profile == null);

        // 3) Gestión de imagen (solo si viene una nueva)
        if (profileImageFile != null && !profileImageFile.isEmpty()) {

            // 3.1 Validaciones
            validateProfileImage(profileImageFile);

            // Imagen anterior (fuente de verdad: la entidad)
            String oldImagePath = profileDTO.getProfileImage();

            // Guardar nueva imagen
            String newImageWebPath = fileStorageService.saveFile(profileImageFile);
            if (newImageWebPath == null || newImageWebPath.isBlank()) {
                // Se lanza la excepción (resource, field, value, detail)
                throw new InvalidFileException(
                        "userProfile",
                        "profileImageFile",
                        profileImageFile.getOriginalFilename(),
                        "No se pudo guardar la imagen de perfil."
                );
            }

            profileDTO.setProfileImage(newImageWebPath);

            // Borrar imagen anterior si existía
            if (oldImagePath != null && !oldImagePath.isBlank()) {
                fileStorageService.deleteFile(oldImagePath);
            }
        }

        // 4) Crear o actualizar entidad
        if (isNew) {
            profile = UserProfileMapper.toNewEntity(profileDTO, user);
        } else {
            UserProfileMapper.copyToExistingEntity(profileDTO, profile);
        }

        // 5) Persistir
        userProfileRepository.save(profile);
    }


    private void validateProfileImage(MultipartFile file) {
        String contentType = file.getContentType();
        // MIME inválido
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileException(
                    "userProfile",
                    "profileImageFile",
                    contentType,
                    "Tipo de archivo no permitido"
            );
        }

        // Tamaño excedido
        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new InvalidFileException(
                    "userProfile",
                    "profileImageFile",
                    file.getSize(),
                    "Archivo demasiado grande (máximo " + MAX_IMAGE_SIZE_BYTES + " bytes)"
            );
        }
    }
}
