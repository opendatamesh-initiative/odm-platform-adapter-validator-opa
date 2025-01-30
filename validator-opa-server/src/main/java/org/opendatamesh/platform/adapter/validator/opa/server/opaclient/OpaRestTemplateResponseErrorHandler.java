package org.opendatamesh.platform.adapter.validator.opa.server.opaclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.errors.opa.OpaErrorResource;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class OpaRestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().is5xxServerError() ||
                httpResponse.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        String responseBodyString = deserializeOpaError(httpResponse);

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
