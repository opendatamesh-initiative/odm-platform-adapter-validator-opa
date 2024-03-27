package org.opendatamesh.platform.up.policy.engine.opa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.core.commons.clients.ODMIntegrationTest;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClientImpl;
import org.opendatamesh.platform.up.policy.engine.opa.server.PolicyEngineOpaApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;
import java.io.File;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { PolicyEngineOpaApplication.class })
public abstract class PolicyEngineOpaApplicationIT extends ODMIntegrationTest {

	@LocalServerPort
	protected String port;

	protected PolicyEngineClientImpl policyEngineClient;

	protected ResourceBuilder resourceBuilder;

	protected ObjectMapper mapper;

	protected final String POLICY_1 = "src/test/resources/policies/policy1.json";

	protected final String POLICY_1_UPDATED = "src/test/resources/policies/policy1-updated.json";

	protected final String POLICY_2 = "src/test/resources/policies/policy2.json";

	protected final String POLICY_3 = "src/test/resources/policies/policy3.json";

	protected final String POLICY_VERSIONS = "src/test/resources/policies/policy-versions.json";

	protected final String POLICY_SERVICESTYPE = "src/test/resources/policies/policy-servicestype.json";

	protected final String SUITE_1 = "src/test/resources/suites/suite1.json";

	protected final String DOCUMENT_1 = "src/test/resources/documents/document1.json";

	protected final String DOCUMENT_2 = "src/test/resources/documents/document-servicestype.json";

	protected final String DPD = "src/test/resources/documents/dpd.json";

	protected static final String OPA_DOCKER_IMAGE = "src/test/resources/opa/docker-compose.yml";

	// private/protected to make it recreate for each test
	// private/protected static final to make it shared with all tests
	@Container
	protected static final DockerComposeContainer opaServer =
			new DockerComposeContainer(new File(OPA_DOCKER_IMAGE))
					.withExposedService("opa", 8181);

	@DynamicPropertySource
	static void opaServerProperties(DynamicPropertyRegistry registry) {
		String baseUrl = "http://localhost:" + opaServer.getServicePort("opa", 8181);
		registry.add("opa.url.policies", () -> baseUrl + "/v1/policies");
		registry.add("opa.url.data", () -> baseUrl + "/v1/data");
	}

	@PostConstruct
	public void init() {

		mapper = ObjectMapperFactory.JSON_MAPPER;
		resourceBuilder = new ResourceBuilder();
		policyEngineClient = new PolicyEngineClientImpl("http://localhost:" + port, mapper);

	}

	// ======================================================================================
	// Verify test basic resources
	// ======================================================================================

	protected ResponseEntity verifyResponseEntity(ResponseEntity responseEntity, HttpStatus statusCode,
												  boolean checkBody) {
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(statusCode);
		if (checkBody) {
			AssertionsForClassTypes.assertThat(responseEntity.getBody()).isNotNull();
		}
		return responseEntity;
	}

	protected void verifyResponseError(
			ResponseEntity<ErrorRes> errorResponse,
			HttpStatus status,
			PolicyApiStandardErrors error
	) {
		assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
		AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
		AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
	}

	protected void verifyResponseError(
			ResponseEntity<ErrorRes> errorResponse,
			HttpStatus status,
			PolicyApiStandardErrors error,
			String message
	) {
		assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
		AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
		AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
		AssertionsForClassTypes.assertThat(errorResponse.getBody().getMessage()).isEqualTo(message);
	}

}
