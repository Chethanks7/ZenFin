package com.ZenFin.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;

    public void sendEmail(MailModel mailModel) throws MessagingException {
        // Using the custom mail sender method to send email
        sendMail(mailModel, 587); // Port 587 for Gmail SMTP with STARTTLS
    }

    private JavaMailSender createCustomMailSender(int port) {
        return new JavaMailSenderImpl() {
            {
                setHost("smtp.gmail.com"); // Correct Gmail SMTP host
                setPort(port);
                setUsername(username); // Add your username here
                setPassword(password); // Add your app password here
                // Additional properties for SSL or TLS
                getJavaMailProperties().put("mail.smtp.auth", "true");
                getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
                // Optional: For debugging
                // getJavaMailProperties().put("mail.debug", "true");
            }
        };
    }

    @Async
    public void sendMail(MailModel mailModel, int port) throws MessagingException {
        JavaMailSender customMailSender = createCustomMailSender(port);

        String templateName = mailModel.getTemplateName() == null ? "confirm-email" : mailModel.getTemplateName().getName();
        MimeMessage message = customMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", mailModel.getUsername());
        properties.put("confirmationUrl", mailModel.getActivationUrl());
        properties.put("activation_code", mailModel.getActivationCode());

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("chethanks545@gmail.com");
        helper.setTo(mailModel.getTo());
        helper.setSubject(mailModel.getSubject());

        String html = templateEngine.process(templateName, context);
        helper.setText(html, true);

        customMailSender.send(message); // Use the custom mail sender
    }
}
