package org.opendatamesh.platform.adapter.validator.opa.server.opaclient;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.EvaluationRequestBody;
import org.springframework.web.client.RestTemplate;

public class OpaClient {

    RestTemplate restTemplate;

    String policiesUrl;

    String dataUrl;

    Long timeout;

    public OpaClient(String policiesUrl, String dataUrl, Long timeout) {
        this.policiesUrl = policiesUrl;
        this.dataUrl = dataUrl;
        this.timeout = timeout;
        this.restTemplate = new OpaRestTemplate(timeout).buildRestTemplate();
    }

    public void createPolicy(String path, String policy) {
        restTemplate.put(policiesUrl + "/" + path, policy);
    }

    public void deletePolicy(String path) {
        restTemplate.delete(policiesUrl + "/" + path);
    }

    public JsonNode validatePolicy(String path, EvaluationRequestBody document) {
        return restTemplate.postForObject(dataUrl + "/" + path, document, JsonNode.class);
    }

}
