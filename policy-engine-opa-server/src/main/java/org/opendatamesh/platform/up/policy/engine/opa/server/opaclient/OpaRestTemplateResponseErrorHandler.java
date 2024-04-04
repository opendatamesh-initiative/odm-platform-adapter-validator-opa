package org.opendatamesh.platform.up.policy.engine.opa.server.opaclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.errors.OpaErrorResource;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.errors.PolicyEngineOpaErrors;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

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
        String responseBodyString = deserializeOpaError(httpResponse);
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

    private String deserializeOpaError(ClientHttpResponse httpResponse) {
        String opaError = "no message available";
        try {
            if (httpResponse.getBody() != null) {
                OpaErrorResource errorResource = objectMapper.readValue(httpResponse.getBody(), OpaErrorResource.class);
                opaError = objectMapper.writeValueAsString(errorResource);
            }
        } catch (JsonProcessingException e) {
            // Error serializing body as OpaErrorResource and then as String
            opaError = "Error serializing OPA response as String";
        } catch (IOException e) {
            // Error extracting body from response
            opaError = "Unknown OPA error";
        }
        return opaError;
    }

}
