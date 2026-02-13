package org.iesalixar.daw2.alvarosegovia.dwese2526_ticket_logger_api_alvarosegovia.services;

import java.util.Locale;
import java.util.Map;

public interface MailService {
    void sendText(String to, String subject, String text);
    void sendHtml(String to, String subject, String html);
    void sendTemplate(String to,
                      String subjectKey,
                      String templateName,
                      Map<String, Object> variables,
                      Locale locale);
}

