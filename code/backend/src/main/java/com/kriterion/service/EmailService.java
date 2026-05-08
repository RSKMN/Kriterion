package com.kriterion.service;

import com.kriterion.entity.User;
import com.kriterion.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final TemplateEngine templateEngine;

    @Async
    public void sendNotificationEmail(Long userId, String subject, String message) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                sendEmail(user.getEmail(), subject, message);
            } else {
                log.warn("User {} has no email address configured, skipping email notification", userId);
            }
        });
    }

    @Async
    public void sendHtmlNotificationEmail(Long userId, String subject, String templateName, Map<String, Object> templateModel) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                sendHtmlEmail(user.getEmail(), subject, templateName, templateModel);
            } else {
                log.warn("User {} has no email address configured, skipping HTML email notification", userId);
            }
        });
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@kriterion.com");
            message.setTo(to);
            message.setSubject("[Kriterion] " + subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Simple email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            Context context = new Context();
            context.setVariables(templateModel);
            
            String htmlContent = templateEngine.process(templateName, context);
            
            helper.setFrom("noreply@kriterion.com");
            helper.setTo(to);
            helper.setSubject("[Kriterion] " + subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to {} using template {}", to, templateName);
        } catch (Exception e) {
            log.error("Failed to send HTML email to {} using template {}: {}", to, templateName, e.getMessage());
        }
    }
}
