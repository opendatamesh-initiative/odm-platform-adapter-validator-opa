package org.opendatamesh.platform.adapter.validator.opa.server.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class EvaluationRequestResponse {

    @JsonProperty("decision_id")
    private String decisionId;

    private Boolean allow;

    @JsonProperty("result")
    private Map<String, Object> result;

    @JsonProperty("result")
    public void setResult(Map<String, Object> result) {
        // Custom setter to extract allow from result sub-object
        this.result = result;
        try {
            this.allow = (boolean) result.get("allow");
        } catch (Exception e) {
            this.allow = null;
        }
    }

}
