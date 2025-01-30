package org.opendatamesh.platform.adapter.validator.opa.server.resources.validator;

import io.swagger.v3.oas.annotations.media.Schema;

public class PolicyEvaluationResultRes {
    @Schema(description = "Policy Evaluation ID to reconcile the evaluation result with the triggering request")
    private Long policyEvaluationId;

    @Schema(description = "Synthetic results stating if the document is valid or not against the provided policy")
    private Boolean evaluationResult;

    @Schema(description = "Extended result of the evaluation")
    private Object outputObject;

    public Long getPolicyEvaluationId() {
        return policyEvaluationId;
    }

    public void setPolicyEvaluationId(Long policyEvaluationId) {
        this.policyEvaluationId = policyEvaluationId;
    }

    public Boolean getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(Boolean evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public Object getOutputObject() {
        return outputObject;
    }

    public void setOutputObject(Object outputObject) {
        this.outputObject = outputObject;
    }
}
