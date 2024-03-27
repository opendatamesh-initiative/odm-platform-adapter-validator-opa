package org.opendatamesh.platform.up.policy.engine.opa;

import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.up.policy.engine.opa.server.PolicyEngineOpaApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@ExtendWith(SpringExtension.class)
@Testcontainers
//@ActiveProfiles("dev")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { PolicyEngineOpaApplication.class })
public abstract class PolicyEngineOpaApplicationIT {

	@LocalServerPort
	protected String port;

	protected ResourceBuilder resourceBuilder;

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
}
