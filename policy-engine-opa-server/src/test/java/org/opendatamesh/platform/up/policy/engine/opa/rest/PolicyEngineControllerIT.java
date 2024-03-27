package org.opendatamesh.platform.up.policy.engine.opa.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.opendatamesh.platform.up.policy.engine.opa.PolicyEngineOpaApplicationIT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyEngineControllerIT extends PolicyEngineOpaApplicationIT {

    @Test
    public void evaluateDocument() throws IOException {

        // Define a policy
        PolicyResource policy = resourceBuilder.readResourceFromFile(POLICY_1, PolicyResource.class);
        // Define an object
        Object objectToEvaluate = resourceBuilder.readResourceFromFile(DOCUMENT_1, Object.class);

        // Create the request body object
        DocumentResource document = new DocumentResource();
        document.setPolicyEvaluationId(1L);
        document.setPolicy(policy);
        document.setObjectToEvaluate(objectToEvaluate);

        // Call the API
        ResponseEntity<ObjectNode> responseEntity = policyEngineClient.evaluateDocumentResponseEntity(document);
        // Assert the response
        verifyResponseEntity(responseEntity, HttpStatus.OK, true);
        EvaluationResource evaluationResource = mapper.convertValue(responseEntity.getBody(), EvaluationResource.class);
        // Assert the body of the response
        assertThat(evaluationResource.getPolicyEvaluationId()).isEqualTo(1L);
        assertThat(evaluationResource.getEvaluationResult()).isTrue();
        assertThat(evaluationResource.getOutputObject()).isNotNull();
    }

}
