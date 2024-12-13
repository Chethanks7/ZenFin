package com.ZenFin.email;

import com.azure.identity.AzureCliCredential;
import com.azure.identity.AzureCliCredentialBuilder;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final SpringTemplateEngine templateEngine;
    private GraphServiceClient<Request> graphClient;

    private void initializeGraphClient() {
        // Define the scopes for sending email
        String[] scopes = new String[]{"Mail.Send"};

        // Create an AzureCliCredential instance to obtain a token
        AzureCliCredential azureCliCredential = new AzureCliCredentialBuilder().build();


        // Create Authentication Provider with Azure CLI credential
        IAuthenticationProvider authProvider = request -> {
            // Create a CompletableFuture to return the token
            CompletableFuture<String> future = new CompletableFuture<>();

            // Use the AzureCliCredential to obtain the token
            try {
                // Get the token asynchronously for the Microsoft Graph API or any other API
                // Handle any errors that occur during the token retrieval
                azureCliCredential.getToken(new com.azure.core.credential.TokenRequestContext().addScopes(scopes))
                        .subscribe(token -> {
                            // When the token is retrieved, complete the future with the token
                            future.complete(token.getToken());
                        }, future::completeExceptionally);
            } catch (Exception e) {
                // Handle any unexpected errors
                future.completeExceptionally(e);
            }

            // Return the future containing the authorization token
            return future;
        };

        // Initialize the Graph client with the authentication provider
        graphClient = GraphServiceClient
                .builder()
                .authenticationProvider(authProvider)
                .buildClient();
    }

    public void sendMail(MailModel mailModel) {
        String templateName = mailModel.getTemplateName() == null
                ? "confirm-email"
                : mailModel.getTemplateName().getName();
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
        message.subject = mailModel.getSubject();

        // Set the body of the email
        ItemBody body = new ItemBody();
        body.contentType = BodyType.HTML;
        body.content = htmlContent;
        message.body = body;

        // Set the recipient
        Recipient recipient = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = mailModel.getTo();
        recipient.emailAddress = emailAddress;
        message.toRecipients = Collections.singletonList(recipient);

        UserSendMailParameterSet parameterSet = new UserSendMailParameterSet();
        parameterSet.message = message;


        // Send the email via Graph API
        sendMessage(parameterSet);
    }

    private void sendMessage(UserSendMailParameterSet message) {
        try {
            graphClient
                    .me()
                    .sendMail(message)  // 'true' to save a copy in the Sent Items
                    .buildRequest()
                    .post();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email via Microsoft Graph API", e);
        }
    }
}
