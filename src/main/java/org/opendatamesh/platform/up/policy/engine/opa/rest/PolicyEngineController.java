package org.opendatamesh.platform.up.policy.engine.opa.rest;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.up.policy.api.v1.controllers.AbstractPolicyEngineController;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.PolicyResource;
import org.opendatamesh.platform.up.policy.engine.opa.opaclient.v1.EvaluationRequestBody;
import org.opendatamesh.platform.up.policy.engine.opa.resources.errors.PolicyEngineOpaErrors;
import org.opendatamesh.platform.up.policy.engine.opa.services.EvaluationService;
import org.opendatamesh.platform.up.policy.engine.opa.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.net.ConnectException;
import java.util.Map;

public class PolicyEngineController extends AbstractPolicyEngineController {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public ResponseEntity<EvaluationResource> evaluateDocument(DocumentResource document) {
        try {

            savePolicyOnOpaServer(document.getPolicy());
            Map opaResult = requestEvaluationToOpaServer(document);
            deletePolicyFromOpaServer(document.getPolicy().getId());

            EvaluationResource responseBody = new EvaluationResource();
            responseBody.setPolicyEvaluationId(document.getPolicyEvaluationId());
            responseBody.setEvaluationResult(Boolean.valueOf((String)opaResult.get("result")));
            responseBody.setOutputObject(opaResult);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseBody);

        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode()==400) {
                throw new BadRequestException(
                        PolicyEngineOpaErrors.SC400_OPA_SERVER_BAD_REQUEST,
                        e.getMessage()
                );
            } else if (e.getRawStatusCode()==500) {
                throw new InternalServerException(
                        PolicyEngineOpaErrors.SC500_OPA_SERVER_INTERNAL_SERVER_ERROR,
                        e.getMessage()
                );
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            if (e.getCause() instanceof ConnectException) {
                // handle connect exception
                throw new InternalServerException(
                        PolicyEngineOpaErrors.SC500_OPA_SERVER_NOT_REACHABLE,
                        e.getMessage()
                );
            }
            throw e;
        }
    }

    private void savePolicyOnOpaServer(PolicyResource policy) {
        policyService.putPolicy(policy.getId(), policy.getRawPolicy());
    }

    private Map requestEvaluationToOpaServer(DocumentResource document) {
        EvaluationRequestBody evaluationRequest = new EvaluationRequestBody();
        evaluationRequest.setInput(document.getObjectToEvaluate());
        return evaluationService.validateByPolicyId(document.getPolicy().getId(), evaluationRequest);
    }

    private void deletePolicyFromOpaServer(String policyId) {
        policyService.deletePolicyById(policyId);
    }
}
