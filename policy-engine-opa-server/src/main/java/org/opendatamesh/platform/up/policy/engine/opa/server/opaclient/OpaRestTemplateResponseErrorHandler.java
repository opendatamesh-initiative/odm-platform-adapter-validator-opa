package org.opendatamesh.platform.up.policy.engine.opa.server.opaclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.errors.PolicyEngineOpaErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class OpaRestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().is5xxServerError() ||
                httpResponse.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        String responseBodyString = "no message available";
        if (httpResponse.getBody() != null) {
            JsonNode responseBody = objectMapper.readTree(httpResponse.getBody());
            responseBodyString = responseBody.toString();
        }
        if (httpResponse.getRawStatusCode() == 503) {
            throw new InternalServerException(
                    PolicyEngineOpaErrors.SC500_02_OPA_SERVER_NOT_REACHABLE,
                    responseBodyString
            );
        } else if (httpResponse.getStatusCode().is5xxServerError()) {
            //Handle SERVER_ERROR
            throw new InternalServerException(
                    PolicyEngineOpaErrors.SC500_01_OPA_SERVER_INTERNAL_SERVER_ERROR,
                    responseBodyString
            );
        } else if (httpResponse.getStatusCode().is4xxClientError()) {
            //Handle CLIENT_ERROR
            throw new BadRequestException(
                    PolicyEngineOpaErrors.SC400_OPA_SERVER_BAD_REQUEST,
                    responseBodyString
            );
        }
    }
}
