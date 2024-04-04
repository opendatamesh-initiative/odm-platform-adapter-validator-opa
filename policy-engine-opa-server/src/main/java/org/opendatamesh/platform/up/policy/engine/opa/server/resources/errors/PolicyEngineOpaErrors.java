package org.opendatamesh.platform.up.policy.engine.opa.server.resources.errors;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum PolicyEngineOpaErrors implements ODMApiStandardErrors {

    SC400_OPA_SERVER_BAD_REQUEST ("40005", "OPA Bad Request error."),
    SC422_01_UNPROCESSABLE_POLICY("42201", "Unprocessable policy."),
    SC422_02_UNPROCESSABLE_INPUT_OBJECT("42202", "Unprocessable input object."),
    SC422_03_POLICY_SYNTAX_IS_INVALID("42203", "Policy syntax is invalid."),
    SC500_01_OPA_SERVER_INTERNAL_SERVER_ERROR ("50001", "OPA Internal Server Error."),
    SC500_02_OPA_SERVER_NOT_REACHABLE ("50002", "OPA Server not reachable."),
    SC500_03_POLICY_ENGINE_OPA_SERVICE_ERROR("50003", "Policy Engine OPA Internal Server Error."),
    SC000_TBD ("00000", "TBD");

    private final String code;
    private final String description;

    PolicyEngineOpaErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}