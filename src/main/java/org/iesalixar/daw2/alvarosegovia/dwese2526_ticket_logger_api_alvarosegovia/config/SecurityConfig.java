package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.config;

import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.config.filters.JwtAuthenticationFilter;
import org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configura la seguridad de la aplicación, definiendo autenticación y autorización
 * para diferentes roles de usuario, y gestionando la política de sesiones.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)  // Activa la seguridad basada en métodos
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura el filtro de seguridad para las solicitudes HTTP, especificando las
     * rutas permitidas y los roles necesarios para acceder a diferentes endpoints.
     *
     * @param http instancia de {@link HttpSecurity} para configurar la seguridad.
     * @return una instancia de {@link SecurityFilterChain} que contiene la configuración de seguridad.
     * @throws Exception si ocurre un error en la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Entrando en el método securityFilterChain");

        // Configuración de seguridad
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Forbidden\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error", "/error/**").permitAll()

                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/regions/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/provinces/**").hasRole("MANAGER")
                        .requestMatchers("/api/profile/**").hasRole("USER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura el codificador de contraseñas para cifrar las contraseñas de los usuarios
     * utilizando BCrypt.
     *
     * @return una instancia de {@link PasswordEncoder} que utiliza BCrypt para cifrar contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Entrando en el método passwordEncoder");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        logger.info("Saliendo del método passwordEncoder");
        return encoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

