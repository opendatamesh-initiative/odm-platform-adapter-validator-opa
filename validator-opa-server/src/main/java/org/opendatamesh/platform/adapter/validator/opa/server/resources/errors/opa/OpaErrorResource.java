package org.opendatamesh.platform.adapter.validator.opa.server.resources.errors.opa;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpaErrorResource {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<OpaErrorErrors> errors;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OpaErrorErrors> getErrors() {
        return errors;
    }

    public void setErrors(List<OpaErrorErrors> errors) {
        this.errors = errors;
    }
}
