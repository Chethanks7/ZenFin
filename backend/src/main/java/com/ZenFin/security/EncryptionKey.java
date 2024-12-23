package com.ZenFin.security;


import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.http.*;
import com.azure.core.util.Context;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class EncryptionKey {


    private static final String VAULT_URL = "https://zenfinkey.vault.azure.net";
    private final String clientId = System.getenv("AZURE_CLIENT_ID");
    private final String tenantId = System.getenv("AZURE_TENANT_ID");
    private final String clientSecret = System.getenv("AZURE_CLIENT_SECRET");

    public String getEncryptionKey(String secretName) {


        TokenCredential credential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .clientSecret(clientSecret)
                .build();

        AccessToken token = Objects.requireNonNull(credential.getToken(new TokenRequestContext().addScopes("https://vault.azure.net/.default")).block());

        HttpRequest request = new HttpRequest(HttpMethod.GET, "https://zenfinkey.vault.azure.net/secrets/"+secretName+"?api-version=7.2")
                .setHeader(HttpHeaderName.fromString("Authorization"), "Bearer " + token.getToken());

        HttpPipeline pipeline = new HttpPipelineBuilder()
                .policies(new BearerTokenAuthorizationPolicy(token.getToken()))
                .build();


        Mono<HttpResponse> response = pipeline.send(request, Context.NONE);
        String responseBody = Objects.requireNonNull(response.block()).getBodyAsString().block();


        assert responseBody != null;
        JsonObject object = JsonParser.parseString(responseBody).getAsJsonObject();
        return object.get("value").getAsString();
    }

}
