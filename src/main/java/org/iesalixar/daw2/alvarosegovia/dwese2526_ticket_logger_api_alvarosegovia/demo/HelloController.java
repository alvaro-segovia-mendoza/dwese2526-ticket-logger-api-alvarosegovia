package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    // Creación del logger para esta clase,
    // nos permitirá registrar mensajes en diferentes niveles (INFO, DEBUG, ERRO, etc.)
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {

        // Loguea cuando el método hello es llamado, mostrando el nombre recibido
        logger.info("Request received to /hello endpoint with parametrer name: {}", name);

        // Formatea y devuelve el mensaje de saludo
        String greeting = String.format("Hello %s!", name);

        // Loguea el mensaje de saludo que se va a devolver
        logger.debug("Greeting message to be returned: {}", greeting);

        return greeting;
    }
}
