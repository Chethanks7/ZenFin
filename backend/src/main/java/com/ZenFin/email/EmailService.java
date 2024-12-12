package com.ZenFin.email;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.beta.models.*;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;
import com.microsoft.graph.beta.users.item.sendmail.SendMailPostRequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final SpringTemplateEngine templateEngine;
    private GraphServiceClient graphClient;

    @Value("${spring.security.oauth2.client.registration.azure.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.azure.client-secret}")
    private String clientSecret;

    @Value("${microsoft.azure.tenant-id}")
    private String tenantId;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendEmail(MailModel mailModel) {
        initializeGraphClient();
        sendMailGraphApi(mailModel);
    }

    private void initializeGraphClient() {

        String[] scopes = new String[]{"Mail.Send"};
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();
        graphClient = new GraphServiceClient(clientSecretCredential, scopes);
    }

    private void sendMailGraphApi(MailModel mailModel) {
        // Set the email template name
        String templateName = mailModel.getTemplateName() == null
                ? "confirm-email"
                : mailModel.getTemplateName().getName();

        // Create properties for the email content
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", mailModel.getUsername());
        properties.put("activation_code", mailModel.getActivationCode());
        properties.put("activation_url", mailModel.getActivationUrl());

        // Process the email template to generate HTML content
        Context context = new Context();
        context.setVariables(properties);
        String htmlContent = templateEngine.process(templateName, context);

        // Build the email message
        Message message = new Message();
        message.setSubject(mailModel.getSubject());

        // Set the body of the email
        ItemBody body = new ItemBody();
        body.setContentType(BodyType.Html);
        body.setContent(htmlContent);
        message.setBody(body);

        // Set the recipient
        Recipient recipient = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress(mailModel.getTo());
        recipient.setEmailAddress(emailAddress);
        message.setToRecipients(Collections.singletonList(recipient));

        // Send the email via Graph API
        sendMessage(message);
    }

    private void sendMessage(Message message) {
        SendMailPostRequestBody requestBody = new SendMailPostRequestBody();
        requestBody.setMessage(message);
        try {
            graphClient.me().sendMail().post(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email via Microsoft Graph API", e);
        }
    }
}
