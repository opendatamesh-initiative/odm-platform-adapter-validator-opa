package org.opendatamesh.platform.up.policy.engine.opa.server.opaclient;

import org.opendatamesh.platform.up.policy.engine.opa.server.resources.EvaluationRequestBody;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.EvaluationRequestResponse;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

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

    public Map<String, Object> getPolicies() {
        return restTemplate.getForObject(policiesUrl, Map.class);
    }

    public Map<String, Object> getPolicyById(String id) {
        return restTemplate.getForObject(policiesUrl+"/"+id, Map.class);
    }

    public void updatePolicy(String id, String policy) {
        restTemplate.put(policiesUrl+"/"+id, policy);
    }

    public void deletePolicyById(String id) {
        restTemplate.delete(policiesUrl+"/"+id);
    }

    public EvaluationRequestResponse validateDocumentByPolicyId(String id, EvaluationRequestBody document) {
        return restTemplate.postForObject(dataUrl+"/"+id, document, EvaluationRequestResponse.class);
    }

    public Map<String, Object> validateDocument(EvaluationRequestBody document) {
        return restTemplate.postForObject(dataUrl, document, Map.class);
    }

}
