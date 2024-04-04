package org.opendatamesh.platform.up.policy.engine.opa.server.opaclient;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.errors.PolicyEngineOpaErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class OpaRestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().is5xxServerError() ||
                httpResponse.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getRawStatusCode() == 503) {
            throw new InternalServerException(
                    PolicyEngineOpaErrors.SC500_02_OPA_SERVER_NOT_REACHABLE,
                    httpResponse.getBody().toString()
            );
        } else if (httpResponse.getStatusCode().is5xxServerError()) {
            //Handle SERVER_ERROR
            throw new InternalServerException(
                    PolicyEngineOpaErrors.SC500_01_OPA_SERVER_INTERNAL_SERVER_ERROR,
                    httpResponse.getBody().toString()
            );
        } else if (httpResponse.getStatusCode().is4xxClientError()) {
            //Handle CLIENT_ERROR
            throw new BadRequestException(
                    PolicyEngineOpaErrors.SC400_OPA_SERVER_BAD_REQUEST,
                    httpResponse.getBody().toString()
            );
        }
    }
}
