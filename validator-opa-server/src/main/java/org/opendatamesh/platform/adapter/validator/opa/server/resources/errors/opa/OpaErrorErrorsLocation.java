package org.opendatamesh.platform.adapter.validator.opa.server.resources.errors.opa;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpaErrorErrorsLocation {

    @JsonProperty("code")
    private String code;

    @JsonProperty("row")
    private Long row;

    @JsonProperty("col")
    private Long column;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getRow() {
        return row;
    }

    public void setRow(Long row) {
        this.row = row;
    }

    public Long getColumn() {
        return column;
    }

    public void setColumn(Long column) {
        this.column = column;
    }
}