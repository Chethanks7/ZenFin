package com.ZenFin.security;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class EncryptionKey {


    public String getEncryptionKey(
            String secretName, String projectId

    ) throws IOException {
        SecretManagerServiceClient client = SecretManagerServiceClient.create();

        SecretVersionName versionName = SecretVersionName.of(projectId, secretName, "latest");

        AccessSecretVersionResponse response = client.accessSecretVersion(versionName);

        return response.getPayload().getData().toStringUtf8();


    }

}
