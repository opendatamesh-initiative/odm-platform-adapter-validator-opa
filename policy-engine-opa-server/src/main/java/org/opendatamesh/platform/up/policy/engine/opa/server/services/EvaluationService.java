package org.opendatamesh.platform.up.policy.engine.opa.server.services;

import org.opendatamesh.platform.up.policy.engine.opa.server.opaclient.OpaClient;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.EvaluationRequestBody;
import org.opendatamesh.platform.up.policy.engine.opa.server.resources.EvaluationRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EvaluationService {

    @Autowired
    OpaClient opaClient;

    public EvaluationRequestResponse validateByPolicyId(String id, EvaluationRequestBody document){
        return opaClient.validateDocumentByPolicyId(id, document);
    }

    public Map<String, Object> validate(EvaluationRequestBody document){
        return opaClient.validateDocument(document);
    }

}
