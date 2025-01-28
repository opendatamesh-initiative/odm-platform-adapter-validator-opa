package org.opendatamesh.platform.adapter.validator.opa.server.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class EvaluationRequestResponse {

    @JsonProperty("decision_id")
    private String decisionId;
    private JsonNode result;


    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
    }

    public JsonNode getResult() {
        return result;
    }

    public void setResult(JsonNode result) {
        this.result = result;
    }
}
