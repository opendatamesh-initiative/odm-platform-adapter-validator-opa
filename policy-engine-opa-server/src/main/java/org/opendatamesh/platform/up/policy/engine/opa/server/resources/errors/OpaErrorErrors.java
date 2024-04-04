package org.opendatamesh.platform.up.policy.engine.opa.server.resources.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpaErrorErrors {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("location")
    private OpaErrorErrorsLocation errors;

}
