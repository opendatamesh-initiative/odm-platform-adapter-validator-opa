package org.opendatamesh.platform.up.policy.engine.opa.server.resources.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpaErrorResource {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<OpaErrorErrors> errors;

}
