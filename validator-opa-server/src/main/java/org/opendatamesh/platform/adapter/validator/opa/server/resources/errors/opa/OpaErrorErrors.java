package org.opendatamesh.platform.adapter.validator.opa.server.resources.errors.opa;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpaErrorErrors {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("location")
    private OpaErrorErrorsLocation errors;

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

    public OpaErrorErrorsLocation getErrors() {
        return errors;
    }

    public void setErrors(OpaErrorErrorsLocation errors) {
        this.errors = errors;
    }
}
