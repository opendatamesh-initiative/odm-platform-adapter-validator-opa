package org.opendatamesh.platform.adapter.validator.opa.server.rest;

import org.opendatamesh.platform.adapter.validator.opa.server.services.PolicyService;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.up.policy.api.v1.controllers.AbstractValidatorController;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.EvaluationRequestResponse;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.errors.ValidatorOpaApiErrors;
import org.opendatamesh.platform.adapter.validator.opa.server.services.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidatorController extends AbstractValidatorController {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public EvaluationResource evaluateDocument(DocumentResource document) {
        validateDocument(document);
        policyService.savePolicyOnOpaServer(document.getPolicy());
        EvaluationRequestResponse opaResult = evaluationService.requestEvaluationToOpaServer(document);
        policyService.deletePolicyFromOpaServer(document.getPolicy().getName());
        return prepareEvaluationResource(document, opaResult);
    }

    private void validateDocument(DocumentResource document) {
        if (ObjectUtils.isEmpty(document.getPolicy())) {
            throw new UnprocessableEntityException(
                    ValidatorOpaApiErrors.SC422_01_UNPROCESSABLE_POLICY,
                    "Policy is empty. The policy object must be provided."
            );
        }
        if (ObjectUtils.isEmpty(document.getPolicy().getName())) {
            throw new UnprocessableEntityException(
                    ValidatorOpaApiErrors.SC422_01_UNPROCESSABLE_POLICY,
                    "Policy name is empty. The policy name must be provided."
            );
        }
        if (!StringUtils.hasText(document.getPolicy().getRawContent())) {
            throw new UnprocessableEntityException(
                    ValidatorOpaApiErrors.SC422_01_UNPROCESSABLE_POLICY,
                    "Policy raw content is empty. The policy raw content (Rego policy) must be provided."
            );
        }
        validatePolicyName(document.getPolicy().getName(), document.getPolicy().getRawContent());
        if (ObjectUtils.isEmpty(document.getObjectToEvaluate())) {
            throw new UnprocessableEntityException(
                    ValidatorOpaApiErrors.SC422_02_UNPROCESSABLE_INPUT_OBJECT,
                    "Object to evaluate is empty. The object to validate the policy against must be provided."
            );
        }
    }

    private void validatePolicyName(String policyName, String rawPolicy) {
        String regoPackage = null;
        String rawPolicyFirstLine = rawPolicy.split("\n")[0];
        if (rawPolicyFirstLine.contains("package")) {
            regoPackage = rawPolicyFirstLine.replace("package", "").trim();
        }

        if (StringUtils.isEmpty(regoPackage)) {
            throw new UnprocessableEntityException(
                    ValidatorOpaApiErrors.SC422_03_POLICY_SYNTAX_IS_INVALID,
                    "Missing package declaration in Rego policy. Check syntax."
            );
        }
        if (!regoPackage.equals(policyName)) {
            throw new UnprocessableEntityException(
                    ValidatorOpaApiErrors.SC422_03_POLICY_SYNTAX_IS_INVALID,
                    "Package name in Rego policy [" + regoPackage + "] differs from policy name [" + policyName + "]. Check syntax."
            );
        }
    }

    private static EvaluationResource prepareEvaluationResource(DocumentResource document, EvaluationRequestResponse opaResult) {
        EvaluationResource response = new EvaluationResource();
        response.setPolicyEvaluationId(document.getPolicyEvaluationId());
        if(opaResult.getAllow() != null) {
            response.setEvaluationResult(opaResult.getAllow());
        } else {
            throw new UnprocessableEntityException(
                    ValidatorOpaApiErrors.SC422_03_POLICY_SYNTAX_IS_INVALID,
                    "Error extracting [allow] attribute from OPA response. Check the Rego policy's syntax."
            );
        }
        response.setOutputObject(opaResult);
        return response;
    }

}
