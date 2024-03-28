package org.opendatamesh.platform.up.policy.engine.opa.server.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.annotation.PostConstruct;
import java.util.Map;

@Data
public class EvaluationRequestResponse {

    @JsonProperty("decision_id")
    private String decisionId;

    private Boolean allow;

    @JsonProperty("result")
    private Map<String, Object> result;

    @PostConstruct
    private void setAllow() {
        try {
            this.allow = (boolean) result.get("allow");
        } catch (Exception e) {
            this.allow = null;
        }
    }

}
