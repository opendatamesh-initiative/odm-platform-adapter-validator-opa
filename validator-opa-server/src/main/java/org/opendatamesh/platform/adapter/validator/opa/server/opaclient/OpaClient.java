package org.opendatamesh.platform.adapter.validator.opa.server.opaclient;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.EvaluationRequestBody;
import org.springframework.web.client.RestTemplate;

public class OpaClient {

    RestTemplate restTemplate;

    String policiesUrl;

    String dataUrl;

    String loggingLevel;

    Long timeout;

    public OpaClient(String policiesUrl, String dataUrl, Long timeout, String loggingLevel) {
        this.policiesUrl = policiesUrl;
        this.dataUrl = dataUrl;
        this.timeout = timeout;
        this.restTemplate = new OpaRestTemplate(timeout).buildRestTemplate();
        this.loggingLevel = loggingLevel;
    }

    public void createPolicy(String path, String policy) {
        restTemplate.put(policiesUrl + "/" + path, policy);
    }

    public void deletePolicy(String path) {
        restTemplate.delete(policiesUrl + "/" + path);
    }

    public JsonNode validatePolicy(String path, EvaluationRequestBody document,boolean verbose ) {
        String url = dataUrl + "/" + path;
        if (verbose && loggingLevel != null && !loggingLevel.trim().isEmpty()) {
            url += "?explain=" + loggingLevel + "&pretty=true";
        }
        return restTemplate.postForObject(url, document, JsonNode.class);
    }
}
