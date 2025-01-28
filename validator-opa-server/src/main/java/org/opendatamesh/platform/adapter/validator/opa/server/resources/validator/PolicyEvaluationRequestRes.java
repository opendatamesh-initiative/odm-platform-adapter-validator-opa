package org.opendatamesh.platform.adapter.validator.opa.server.resources.validator;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.policy.PolicyResource;

public class PolicyEvaluationRequestRes {
    private Long policyEvaluationId;
    private PolicyResource policy;
    private JsonNode objectToEvaluate;

    public Long getPolicyEvaluationId() {
        return policyEvaluationId;
    }

    public void setPolicyEvaluationId(Long policyEvaluationId) {
        this.policyEvaluationId = policyEvaluationId;
    }

    public PolicyResource getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyResource policy) {
        this.policy = policy;
    }

    public JsonNode getObjectToEvaluate() {
        return objectToEvaluate;
    }

    public void setObjectToEvaluate(JsonNode objectToEvaluate) {
        this.objectToEvaluate = objectToEvaluate;
    }
}
