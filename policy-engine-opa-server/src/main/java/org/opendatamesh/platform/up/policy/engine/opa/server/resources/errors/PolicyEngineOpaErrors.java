package org.opendatamesh.platform.up.policy.engine.opa.server.resources.errors;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum PolicyEngineOpaErrors implements ODMApiStandardErrors {

    SC400_OPA_SERVER_BAD_REQUEST ("40005", "OPA Bad Request error."),
    SC422_POLICY_SYNTAX_IS_INVALID ("42201", "Policy syntax is invalid"),
    SC500_OPA_SERVER_INTERNAL_SERVER_ERROR ("50001", "OPA Internal Server Error"),
    SC500_OPA_SERVER_NOT_REACHABLE ("50002", "OPA Server not reachable"),
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