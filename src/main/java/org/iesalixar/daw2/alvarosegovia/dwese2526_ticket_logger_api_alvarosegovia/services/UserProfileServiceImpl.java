package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import jakarta.transaction.Transactional;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.UserProfileDTO;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.dto.UserProfilePatchDTO;
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
    public UserProfileDTO getFormByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user", "email", email));

        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(user.getId());
        UserProfile profile = profileOpt.orElse(null);
        return UserProfileMapper.toFormDto(user, profile);
    }

    /**
     * Aplica un PATCH al perfil del usuario autenticado:
     * solo actualiza los campos que vengan en el DTO (no-nulos) y, opcionalmente,
     * sustituye la imagen de perfil si se adjunta un fichero.
     *
     * @param email            email del usuario autenticado (Principal)
     * @param patchDto         datos parciales del perfil (campos nulos => no se modifican)
     * @param profileImageFile imagen opcional (si viene, se valida y se reemplaza)
     * @throws ResourceNotFoundException si no existe el usuario (por email)
     * @throws InvalidFileException     si la imagen no cumple validaciones o no se puede guardar
     */
    @Override
    @Transactional
    public void updateProfile(String email, UserProfilePatchDTO patchDto, MultipartFile profileImageFile) {
        logger.info("Parchando perfil para email={}", email);


        // 1) Fuente de verdad: user por email (NO confiar en userId/email del cliente)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user", "email", email));


        Long userId = user.getId();


        // 2) Cargar perfil; si no existe, crearlo (PATCH upsert)
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });


        // 3) Merge de campos: solo tocar si vienen (no-null)
        if (patchDto.getFirstName() != null) {
            profile.setFirstName(patchDto.getFirstName());
        }
        if (patchDto.getLastName() != null) {
            profile.setLastName(patchDto.getLastName());
        }
        if (patchDto.getPhoneNumber() != null) {
            profile.setPhoneNumber(patchDto.getPhoneNumber());
        }
        if (patchDto.getBio() != null) {
            profile.setBio(patchDto.getBio());
        }
        if (patchDto.getLocale() != null) {
            profile.setLocale(patchDto.getLocale());
        }


        // 4) Imagen: validar + guardar nueva + borrar anterior
        if (profileImageFile != null && !profileImageFile.isEmpty()) {


            // Validaciones semánticas (lanza InvalidFileException si algo no cuadra)
            validateProfileImage(profileImageFile);


            // OJO: la ruta anterior está en la ENTIDAD, no en el DTO de entrada
            String oldImagePath = profile.getProfileImage();


            String newImageWebPath = fileStorageService.saveFile(profileImageFile);
            if (newImageWebPath == null || newImageWebPath.isBlank()) {
                throw new InvalidFileException(
                        "userProfile",
                        "profileImageFile",
                        profileImageFile.getOriginalFilename(),
                        "No se pudo guardar la imagen de perfil."
                );
            }


            profile.setProfileImage(newImageWebPath);


            // Borrar anterior si existía y es distinta (evitas borrarte a ti mismo si reusas nombre)
            if (oldImagePath != null && !oldImagePath.isBlank() && !oldImagePath.equals(newImageWebPath)) {
                fileStorageService.deleteFile(oldImagePath);
            }
        }


        // 5) Persistir (save sirve tanto para nuevo como existente)
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
