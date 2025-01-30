package org.opendatamesh.platform.adapter.validator.opa.rest;

public enum RoutesV1 {
    EVALUATE_POLICY("/evaluate-policy"),
    HEALTH_CHECK("/actuator/health");

    private final String path;

    private static final String CONTEXT_PATH = "/api/v1/up/validator";

    RoutesV1(String path) {
        this.path = CONTEXT_PATH + path;
    }

    public String getPath() {
        return path;
    }
}
