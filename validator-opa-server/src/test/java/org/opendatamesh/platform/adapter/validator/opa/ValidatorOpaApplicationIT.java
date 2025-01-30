package org.opendatamesh.platform.adapter.validator.opa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.adapter.validator.opa.rest.RoutesV1;
import org.opendatamesh.platform.adapter.validator.opa.server.ValidatorOpaApplication;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.errors.ErrorRes;
import org.opendatamesh.platform.adapter.validator.opa.server.rest.exceptions.ValidatorApiException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;
import java.io.File;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ValidatorOpaApplication.class})
public abstract class ValidatorOpaApplicationIT {

    @LocalServerPort
    protected String port;
    protected TestRestTemplate rest;

    protected ResourceBuilder resourceBuilder;

    protected ObjectMapper mapper;

    protected final String POLICY = "src/test/resources/policies/policy1.json";
    protected final String POLICY_WITH_NO_REGO_PACKAGE = "src/test/resources/policies/policy-with-missing-rego-package.json";
    protected final String POLICY_WITH_NO_ALLOW_ATTRIBUTE = "src/test/resources/policies/policy-with-no-allow-attribute.json";

    protected final String DOCUMENT = "src/test/resources/documents/document1.json";

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

        mapper = new ObjectMapper();
        resourceBuilder = new ResourceBuilder();
        rest = new TestRestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000);
        requestFactory.setReadTimeout(30000);
        rest.getRestTemplate().setRequestFactory(requestFactory);
        // add uri template handler because '+' of iso date would not be encoded
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        rest.setUriTemplateHandler(defaultUriBuilderFactory);

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
            ValidatorApiException error
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getStatus()).isEqualTo(error.getStatus());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getMessage()).isEqualTo(error.getMessage());
    }

    protected void verifyResponseError(
            ResponseEntity<ErrorRes> errorResponse,
            HttpStatus status,
            ValidatorApiException error,
            String message
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getStatus()).isEqualTo(error.getStatus());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getMessage()).isEqualTo(error.getMessage());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getMessage()).isEqualTo(message);
    }

    protected String apiUrl(RoutesV1 route) {
        return apiUrl(route, "");
    }

    protected String apiUrl(RoutesV1 route, String extension) {
        return apiUrlFromString(route.getPath() + extension);
    }

    protected String apiUrlFromString(String routeUrlString) {
        return "http://localhost:" + port + routeUrlString;
    }

    protected String apiUrlOfItem(RoutesV1 route) {
        return apiUrl(route, "/{uuid}");
    }
}
