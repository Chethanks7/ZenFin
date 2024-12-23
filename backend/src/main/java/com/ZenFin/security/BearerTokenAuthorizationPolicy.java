package com.ZenFin.security;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.http.*;
import com.azure.core.http.policy.HttpPipelinePolicy;
import reactor.core.publisher.Mono;

public record BearerTokenAuthorizationPolicy(String token) implements HttpPipelinePolicy,TokenCredential{


    @Override
    public Mono<HttpResponse> process(HttpPipelineCallContext httpPipelineCallContext, HttpPipelineNextPolicy  next) {
        HttpRequest request = httpPipelineCallContext.getHttpRequest();

        // Add the Authorization header with the Bearer token
        request.setHeader(HttpHeaderName.fromString("Authorization"), "Bearer " + token);

        // Call the next policy in the pipeline
        return next.process();
    }

    @Override
    public HttpResponse processSync(HttpPipelineCallContext context, HttpPipelineNextSyncPolicy next) {
        return HttpPipelinePolicy.super.processSync(context, next);
    }

    @Override
    public HttpPipelinePosition getPipelinePosition() {
        return HttpPipelinePolicy.super.getPipelinePosition();
    }

    @Override
    public Mono<AccessToken> getToken(TokenRequestContext tokenRequestContext) {
        return null;
    }

    @Override
    public AccessToken getTokenSync(TokenRequestContext request) {
        return TokenCredential.super.getTokenSync(request);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "";
    }
}
