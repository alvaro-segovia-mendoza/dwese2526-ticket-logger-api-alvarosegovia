package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.entities.User;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Servicio de Spring Security encargado de cargar un usuario desde base de datos
 * a partir del identificador introducido en el login.
 *
 * <p><b>Nota:</b> En este proyecto el "username" que introduce el usuario es el <b>email</b>,
 * por lo que se busca el usuario por {@code email} en la tabla {@code users}.</p>
 *
 * <p>Esta versión es la mínima para que el login funcione:
 * <ul>
 *   <li>Obtiene el usuario por email</li>
 *   <li>Devuelve un {@link org.springframework.security.core.userdetails.User} con sus roles</li>
 *   <li>Aplica únicamente el flag de cuenta activa ({@code active}) como "disabled"</li>
 * </ul>
 * </p>
 *
 * <p>Más adelante se añadirán reglas adicionales de ciberseguridad:
 * bloqueo por intentos, expiración de contraseña, email verificado, etc.</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {


    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);


    @Autowired
    private UserRepository userRepository;


    /**
     * Carga los detalles de autenticación/autorización del usuario a partir del "username".
     *
     * <p>En este proyecto {@code username == email}, por lo que se consulta
     * {@link UserRepository#findByEmail(String)}.</p>
     *
     * <p>Devuelve un {@link UserDetails} con:
     * <ul>
     *   <li>{@code username}: email</li>
     *   <li>{@code password}: hash BCrypt almacenado en {@code password_hash}</li>
     *   <li>{@code authorities}: roles tipo {@code ROLE_USER}, {@code ROLE_ADMIN}, etc.</li>
     *   <li>{@code disabled}: basado en {@code active}</li>
     * </ul>
     * </p>
     *
     * @param username email introducido en el formulario de login.
     * @return {@link UserDetails} que Spring Security usará para autenticar y autorizar.
     * @throws UsernameNotFoundException si no existe un usuario con ese email.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Log de entrada (sin datos sensibles; el email es aceptable en muchos entornos,
        // aunque en producción a veces se enmascara).
        log.debug("Entrando en loadUserByUsername(username={})", username);


        final String email = username;


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("No se encontró usuario con email={}", email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });


        // Construimos el UserDetails de Spring Security.
        // OJO: nunca loguear contraseñas ni hashes.
        // Se necesita el org.springframework.security.core.userdetails para poder diferenciarlo de nuestro User
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(
                        user.getRoles().stream()
                                .map(role -> role.getName()) // ROLE_ADMIN, ROLE_USER...
                                .collect(Collectors.toList())
                                .toArray(new String[0])
                )
                // Versión mínima: sin reglas extra aún.
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();


        // Log de salida
        log.debug("Saliendo de loadUserByUsername(email={}) -> authorities={}",
                user.getEmail(),
                userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toList())
        );


        return userDetails;
    }
}





