package com.ZenFin.email;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class GmailService {

    private static final String CLINT_SECRET_FILE="C:\\Users\\User\\Downloads\\my-secret-file.json";
    private static final List<String> SCOPES= Collections.singletonList(GmailScopes.GMAIL_SEND);
    private static final String APPLICATION_NAME = "ZenFin";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String DEFAULT_TEMPLATE = "confirm-email";
    private static final String SENDER_EMAIL = "me";

    private final SpringTemplateEngine templateEngine;


    public  Gmail getGmailService() throws IOException, GeneralSecurityException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                getClientSecret(),
                SCOPES
        ).build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");

        return new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private  GoogleClientSecrets getClientSecret() throws IOException {

        InputStream stream = GmailService.class.getResourceAsStream(CLINT_SECRET_FILE);
        if (stream == null) {
            throw new IllegalStateException("Resource not found: " + GmailService.class.getResource(CLINT_SECRET_FILE));
        }

        return GoogleClientSecrets.load(
                JSON_FACTORY,new InputStreamReader(stream)
        );
    }

    public  void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplateName,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws Exception, MessagingException {
        Gmail service = getGmailService();
        MimeMessage email = createEmail(
                to, username,emailTemplateName,confirmationUrl,activationCode,subject
        );
        sendMessage(service, "me", email);
    }

    private void sendMessage(Gmail service, String userId, MimeMessage email) throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
        service.users().messages().send(userId, message).execute();

    }

    private Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        email.writeTo(outputStream);
        byte[] bytes = outputStream.toByteArray();
        Message message = new Message();
        message.setRaw(Base64.getEncoder().encodeToString(bytes));
        return message;
    }

    private MimeMessage createEmail(
            String to,
            String username,
            EmailTemplateName emailTemplateName,
            String confirmationUrl,
            String activationCode,
            String subject) throws MessagingException {

        // Validate recipient and subject
        if (to == null || to.isEmpty()) {
            throw new IllegalArgumentException("Recipient email (to) must not be null or empty");
        }

        if (subject == null || subject.isEmpty()) {
            throw new IllegalArgumentException("Email subject must not be null or empty");
        }

        // Determine template name
        String templateName = (emailTemplateName == null) ? DEFAULT_TEMPLATE : emailTemplateName.getName();

        // Initialize MimeMessage
        MimeMessage email = new MimeMessage(Session.getDefaultInstance(new Properties()));
        MimeMessageHelper helper = new MimeMessageHelper(
                email,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        // Prepare properties for the template
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("activation_code", activationCode);
        properties.put("activation_url", confirmationUrl);

        // Set template context
        Context context = new Context();
        context.setVariables(properties);

        // Process the template to generate email content
        String html;
        try {
            html = templateEngine.process(templateName, context);
        } catch (Exception e) {
            throw new MessagingException("Failed to process email template: " + templateName, e);
        }

        // Configure email fields
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(SENDER_EMAIL);
        helper.setText(html, true); // Set the email body as HTML

        return email;
    }


}
