package com.ZenFin.security;


import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionKey {


    private static final String VAULT_URL = "https://zenfin-key.vault.azure.net/";

    public String getEncryptionKey(String secretName) {
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(VAULT_URL)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();

        return secretClient.getSecret(secretName).getValue();
    }

}
