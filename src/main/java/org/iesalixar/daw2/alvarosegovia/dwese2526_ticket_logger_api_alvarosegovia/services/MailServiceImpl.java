package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

/**
 * Servicio genérico de envío de correos de la aplicación.
 * <p>
 * Permite enviar emails en texto plano, HTML o basados en plantillas Thymeleaf.
 * Soporta internacionalización (i18n) mediante {@link MessageSource} para el asunto
 * y usa SMTP a través de {@link JavaMailSender}.
 * </p>
 */
@Service
public class MailServiceImpl implements MailService {


    /**
     * Cliente SMTP de Spring (crea y envía mensajes Mime).
     */
    @Autowired
    private JavaMailSender mailSender;


    /**
     * Fuente de mensajes para i18n (asuntos y textos por claves).
     */
    @Autowired
    private MessageSource messageSource;


    /**
     * Motor de plantillas Thymeleaf para renderizar HTML desde templates.
     */
    @Autowired
    private SpringTemplateEngine templateEngine;


    /**
     * Remitente por defecto (application.properties).
     * <p>Con Gmail suele coincidir con {@code spring.mail.username}.</p>
     */
    @Value("${spring.mail.from:}")
    private String defaultFrom;


    /**
     * Envía un email en texto plano.
     *
     * @param to      destinatario
     * @param subject asunto
     * @param text    cuerpo en texto plano
     */
    @Override
    public void sendText(String to, String subject, String text) {
        send(to, subject, text, false);
    }


    /**
     * Envía un email en HTML.
     *
     * @param to      destinatario
     * @param subject asunto
     * @param html    cuerpo en HTML
     */
    @Override
    public void sendHtml(String to, String subject, String html) {
        send(to, subject, html, true);
    }


    /**
     * Envía un email HTML renderizado desde una plantilla Thymeleaf.
     * <p>
     * El asunto se obtiene por i18n usando {@code subjectKey}. El cuerpo se genera
     * renderizando {@code templateName} con las {@code variables} y añadiendo variables base:
     * {@code subject} y {@code lang}.
     * </p>
     *
     * @param to           destinatario
     * @param subjectKey   clave i18n del asunto
     * @param templateName nombre/ruta del template (p.ej. {@code "mail/reset-password"})
     * @param variables    variables del template (title, intro, ctaText, ctaUrl, etc.)
     * @param locale       locale del usuario
     */
    @Override
    public void sendTemplate(String to,
                             String subjectKey,
                             String templateName,
                             Map<String, Object> variables,
                             Locale locale) {


        String subject = messageSource.getMessage(subjectKey, null, locale);


        Context ctx = new Context(locale);
        ctx.setVariables(variables);
        ctx.setVariable("subject", subject);
        ctx.setVariable("lang", locale.getLanguage());


        String html = templateEngine.process(templateName, ctx);
        send(to, subject, html, true);
    }


    /**
     * Envía un {@link MimeMessage} con cuerpo en texto plano o HTML.
     *
     * @param to      destinatario
     * @param subject asunto
     * @param body    cuerpo del mensaje
     * @param isHtml  {@code true} si el cuerpo es HTML; {@code false} si es texto plano
     * @throws IllegalStateException si falla la construcción o el envío por SMTP
     */
    private void send(String to, String subject, String body, boolean isHtml) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, StandardCharsets.UTF_8.name());


            if (defaultFrom != null && !defaultFrom.isBlank()) {
                helper.setFrom(defaultFrom);
            }


            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, isHtml);


            mailSender.send(msg);


        } catch (MessagingException e) {
            throw new IllegalStateException("Email could not be sent.", e);
        }
    }
}

