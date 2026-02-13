package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service encargado de gestionar el almacenamiento de archivos en el sistema de ficheros.
 * Permite guardar archivos subidos por el usuario y eliminar archivos existentes.
 *
 * Este servicio utiliza una ruta base configurada en application.properties mediante la
 * propiedad <strong>app.upload-root</strong>.
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    /**
     * Ruta raíz donde se almacenarán los archivos.
     * Se inyecta desde application.properties con la clave {@code app.upload-root}.
     */
    @Value("${app.upload-root}")
    private String uploadRootPath;

    /** Subdirectorio donde se guardan los archivos subidos. */
    private static final String UPLOADS_SUBDIR = "uploads";

    /**
     * Guarda un archivo enviado mediante formulario multipart.
     *
     * @param file MultipartFile recibido desde el controlador.
     * @return Ruta web accesible del archivo almacenado, o null si hubo error.
     */
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Intento de guardar un archivo vacío o nulo");
            return null;
        }

        try {
            String originalFilename = file.getOriginalFilename();

            // Extraemos la extensión original del archivo
            String fileExtension = getFileExtension(originalFilename);

            // Generamos un nombre totalmente único
            String uniqueFileName = UUID.randomUUID().toString();

            if (!fileExtension.isBlank()) {
                uniqueFileName += "." + fileExtension;
            }

            // Path absoluto hacia /uploads
            Path uploadsDir = Paths.get(uploadRootPath).resolve(UPLOADS_SUBDIR);

            // Creamos directorios si no existen
            Files.createDirectories(uploadsDir);

            // Path completo del archivo nuevo
            Path filePath = uploadsDir.resolve(uniqueFileName);

            // Guardamos el archivo físicamente
            Files.write(filePath, file.getBytes());
            logger.info("Archivo {} guardado con éxito en {}", uniqueFileName, filePath);

            // Devolvemos la ruta accesible desde la web
            return "/uploads/" + uniqueFileName;

        } catch (IOException e) {
            logger.error("Error al guardar el archivo: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Elimina un archivo previamente almacenado.
     *
     * @param filePathOrWebPath Ruta completa o ruta web del archivo (por ejemplo: "/uploads/foto.png").
     */
    public void deleteFile(String filePathOrWebPath) {
        if (filePathOrWebPath == null || filePathOrWebPath.isBlank()) {
            logger.warn("Se ha intentado eliminar un archivo con un nombre/ruta vacío.");
            return;
        }

        try {
            // Extrae solo el nombre del archivo eliminando posibles rutas
            String fileName = normalizeFileName(filePathOrWebPath);

            Path uploadsDir = Paths.get(uploadRootPath).resolve(UPLOADS_SUBDIR);
            Path filePath = uploadsDir.resolve(fileName);

            // Elimina el archivo si existe
            Files.deleteIfExists(filePath);
            logger.info("Archivo {} eliminado con éxito ({})", fileName, filePath);

        } catch (IOException e) {
            logger.error("Error al eliminar el archivo {}: {}", filePathOrWebPath, e.getMessage(), e);
        }
    }

    /**
     * Obtiene la extensión de un archivo.
     *
     * @param fileName Nombre original del archivo.
     * @return Extensión sin el punto (por ejemplo: "png"), o cadena vacía si no hay extensión.
     */
    private String getFileExtension(String fileName) {
        if (fileName != null) {
            int lastDot = fileName.lastIndexOf(".");
            if (lastDot > 0 && lastDot < fileName.length() - 1) {
                return fileName.substring(lastDot + 1);
            }
        }
        return "";
    }

    /**
     * Normaliza un nombre de archivo eliminando rutas web o rutas absolutas.
     * Esto asegura que nunca se permita eliminar archivos fuera de /uploads.
     *
     * @param filePathOrWebPath Ruta completa o ruta web recibida.
     * @return Nombre limpio del archivo.
     */
    private String normalizeFileName(String filePathOrWebPath) {
        String value = filePathOrWebPath.trim();

        // Si viene en formato web "/uploads/xxx"
        if (value.startsWith("/uploads/")) {
            value = value.substring("/uploads/".length());
        }

        // Si viene con ruta completa, nos quedamos solo con el nombre
        int lastSlash = value.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < value.length() - 1) {
            value = value.substring(lastSlash + 1);
        }

        return value;
    }
}
