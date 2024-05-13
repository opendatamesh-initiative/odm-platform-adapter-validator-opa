package org.opendatamesh.platform.adapter.validator.opa.server.resources.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpaErrorErrors {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("location")
    private OpaErrorErrorsLocation errors;

}
