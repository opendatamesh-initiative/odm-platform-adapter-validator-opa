package org.opendatamesh.platform.up.policy.engine.opa.server.rest;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.up.policy.api.v1.controllers.AbstractPolicyEngineController;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.opendatamesh.platform.up.policy.engine.opa.server.opaclient.v1.EvaluationRequestBody;
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
            Map opaResult = requestEvaluationToOpaServer(document);
            deletePolicyFromOpaServer(document.getPolicy().getName());

            EvaluationResource response = new EvaluationResource();
            // TODO: create an object to map the results of OPA + remove explicit cast
            response.setPolicyEvaluationId(document.getPolicyEvaluationId());
            Map opaResultResult = (Map) opaResult.get("result");
            response.setEvaluationResult((Boolean) opaResultResult.get("allow"));
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
        policyService.putPolicy(policy.getName(), policy.getRawContent());
    }

    private Map requestEvaluationToOpaServer(DocumentResource document) {
        EvaluationRequestBody evaluationRequest = new EvaluationRequestBody();
        evaluationRequest.setInput(document.getObjectToEvaluate());
        return evaluationService.validateByPolicyId(document.getPolicy().getName(), evaluationRequest);
    }

    private void deletePolicyFromOpaServer(String policyId) {
        policyService.deletePolicyById(policyId);
    }

}
