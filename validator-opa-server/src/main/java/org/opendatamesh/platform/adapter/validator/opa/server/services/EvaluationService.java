package org.opendatamesh.platform.adapter.validator.opa.server.services;

import org.opendatamesh.platform.up.validator.api.resources.DocumentResource;
import org.opendatamesh.platform.adapter.validator.opa.server.opaclient.OpaClient;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.EvaluationRequestBody;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.EvaluationRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EvaluationService {

    @Autowired
    OpaClient opaClient;

    public EvaluationRequestResponse requestEvaluationToOpaServer(DocumentResource document) {
        EvaluationRequestBody evaluationRequest = new EvaluationRequestBody();
        evaluationRequest.setInput(document.getObjectToEvaluate());
        return validateByPolicyId(document.getPolicy().getName(), evaluationRequest);
    }

    private EvaluationRequestResponse validateByPolicyId(String id, EvaluationRequestBody document){
        return opaClient.validateDocumentByPolicyId(id, document);
    }

    private Map<String, Object> validate(EvaluationRequestBody document){
        return opaClient.validateDocument(document);
    }

}
