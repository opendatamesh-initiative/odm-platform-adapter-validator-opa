package org.opendatamesh.platform.up.policy.engine.opa.services;

import org.opendatamesh.platform.up.policy.engine.opa.opaclient.OpaClient;
import org.opendatamesh.platform.up.policy.engine.opa.opaclient.v1.EvaluationRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EvaluationService {

    @Autowired
    OpaClient opaClient;

    public Map validateByPolicyId(String id, EvaluationRequestBody document){
        return opaClient.validateDocumentByPolicyId(id, document);
    }

    public Map<String, Object> validate(EvaluationRequestBody document){
        return opaClient.validateDocument(document);
    }

}
