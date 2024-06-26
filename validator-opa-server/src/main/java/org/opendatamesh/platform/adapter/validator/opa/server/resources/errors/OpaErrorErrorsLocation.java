package org.opendatamesh.platform.adapter.validator.opa.server.resources.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpaErrorErrorsLocation {

    @JsonProperty("code")
    private String code;

    @JsonProperty("row")
    private Long row;

    @JsonProperty("col")
    private Long column;

}