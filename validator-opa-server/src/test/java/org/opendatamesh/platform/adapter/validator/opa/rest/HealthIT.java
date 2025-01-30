package org.opendatamesh.platform.adapter.validator.opa.rest;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.adapter.validator.opa.ValidatorOpaApplicationIT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthIT extends ValidatorOpaApplicationIT {

    @Test
    public void testHealthCheck() {
        ResponseEntity<String> responseEntity = rest.getForEntity(apiUrl(RoutesV1.HEALTH_CHECK), String.class);
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

    }
}
