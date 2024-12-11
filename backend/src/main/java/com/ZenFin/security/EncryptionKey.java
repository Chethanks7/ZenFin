package com.ZenFin.security;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class EncryptionKey {


    public String getEncryptionKey(
            String secreteId

    ) throws IOException {
        SecretManagerServiceClient client = SecretManagerServiceClient.create();

        AccessSecretVersionResponse response = client.accessSecretVersion(secreteId);

        return response.getPayload().getData().toStringUtf8();


    }

}
