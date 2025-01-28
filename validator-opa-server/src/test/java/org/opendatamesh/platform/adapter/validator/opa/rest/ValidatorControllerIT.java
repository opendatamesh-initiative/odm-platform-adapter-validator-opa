package org.opendatamesh.platform.adapter.validator.opa.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.adapter.validator.opa.ValidatorOpaApplicationIT;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.policy.PolicyResource;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.validator.PolicyEvaluationRequestRes;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.validator.PolicyEvaluationResultRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class ValidatorControllerIT extends ValidatorOpaApplicationIT {

    @Test
    public void evaluateDocument() throws IOException {

        // Define a policy
        PolicyResource policy = resourceBuilder.readResourceFromFile(POLICY, PolicyResource.class);
        // Define an object
        Object objectToEvaluate = resourceBuilder.readResourceFromFile(DOCUMENT, Object.class);

        // Create the request body object
        PolicyEvaluationRequestRes document = new PolicyEvaluationRequestRes();
        document.setPolicyEvaluationId(1L);
        document.setPolicy(policy);
        document.setObjectToEvaluate(mapper.valueToTree(objectToEvaluate));

        // Call the API
        ResponseEntity<ObjectNode> responseEntity = rest.postForEntity(apiUrl(RoutesV1.EVALUATE_POLICY), document, ObjectNode.class);
        // Assert the response
        verifyResponseEntity(responseEntity, HttpStatus.OK, true);
        PolicyEvaluationResultRes evaluationResource = mapper.convertValue(responseEntity.getBody(), PolicyEvaluationResultRes.class);
        // Assert the body of the response
        assertThat(evaluationResource.getPolicyEvaluationId()).isEqualTo(1L);
        assertThat(evaluationResource.getEvaluationResult()).isTrue();
        assertThat(evaluationResource.getOutputObject()).isNotNull();
    }
}
