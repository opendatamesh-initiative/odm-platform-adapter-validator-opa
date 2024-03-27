package org.opendatamesh.platform.up.policy.engine.opa.rest;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.opendatamesh.platform.up.policy.engine.opa.PolicyEngineOpaApplicationIT;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyEngineControllerIT extends PolicyEngineOpaApplicationIT {

    private TestRestTemplate rest;

    @Test
    public void evaluateDocument() throws IOException {
        // define a policy
        PolicyResource policy = resourceBuilder.readResourceFromFile(POLICY_1,PolicyResource.class);
        // define an object
        Object objectToEvaluate = resourceBuilder.readResourceFromFile(DOCUMENT_1, Object.class);
        // create the request body object
        DocumentResource document = new DocumentResource();
        document.setPolicyEvaluationId(1L);
        document.setPolicy(policy);
        document.setObjectToEvaluate(objectToEvaluate);
        // call the API
        ResponseEntity<EvaluationResource> responseEntity = rest.postForEntity(
                "",
                document,
                EvaluationResource.class
        );
        // assert the response
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getEvaluationResult()).isNotNull();
    }
}
