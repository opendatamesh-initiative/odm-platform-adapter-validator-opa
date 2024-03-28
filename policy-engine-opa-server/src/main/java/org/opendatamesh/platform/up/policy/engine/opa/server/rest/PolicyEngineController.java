package org.opendatamesh.platform.up.policy.engine.opa.server.rest;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.up.policy.api.v1.controllers.AbstractPolicyEngineController;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.errors.PolicyEngineApiStandardErrors;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.EvaluationRequestBody;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.EvaluationRequestResponse;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.errors.PolicyEngineOpaErrors;
import org.opendatamesh.platform.up.policy.engine.opa.server.services.EvaluationService;
import org.opendatamesh.platform.up.policy.engine.opa.server.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.net.ConnectException;
import java.util.Map;

@RestController
public class PolicyEngineController extends AbstractPolicyEngineController {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public EvaluationResource evaluateDocument(DocumentResource document) {
        try {

            savePolicyOnOpaServer(document.getPolicy());
            EvaluationRequestResponse opaResult = requestEvaluationToOpaServer(document);
            deletePolicyFromOpaServer(document.getPolicy().getName());

            EvaluationResource response = new EvaluationResource();
            response.setPolicyEvaluationId(document.getPolicyEvaluationId());
            if(opaResult.getAllow() != null) {
                response.setEvaluationResult(opaResult.getAllow());
            } else {
                throw new InternalServerException(
                        PolicyEngineOpaErrors.SC500_03_POLICY_ENGINE_OPA_SERVICE_ERROR,
                        "Error extracting [allow] attributres from OPA response"
                );
            }
            response.setOutputObject(opaResult);

            return response;

        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode()==400) {
                throw new BadRequestException(
                        PolicyEngineOpaErrors.SC400_OPA_SERVER_BAD_REQUEST,
                        e.getMessage()
                );
            } else if (e.getRawStatusCode()==500) {
                throw new InternalServerException(
                        PolicyEngineOpaErrors.SC500_01_OPA_SERVER_INTERNAL_SERVER_ERROR,
                        e.getMessage()
                );
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            if (e.getCause() instanceof ConnectException) {
                // handle connect exception
                throw new InternalServerException(
                        PolicyEngineOpaErrors.SC500_02_OPA_SERVER_NOT_REACHABLE,
                        e.getMessage()
                );
            }
            throw e;
        }
    }

    private void savePolicyOnOpaServer(PolicyResource policy) {
        policyService.putPolicy(policy.getName(), policy.getRawContent());
    }

    private EvaluationRequestResponse requestEvaluationToOpaServer(DocumentResource document) {
        EvaluationRequestBody evaluationRequest = new EvaluationRequestBody();
        evaluationRequest.setInput(document.getObjectToEvaluate());
        return evaluationService.validateByPolicyId(document.getPolicy().getName(), evaluationRequest);
    }

    private void deletePolicyFromOpaServer(String policyId) {
        policyService.deletePolicyById(policyId);
    }

}
