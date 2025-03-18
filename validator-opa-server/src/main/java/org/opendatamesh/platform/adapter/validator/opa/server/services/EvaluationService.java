package org.opendatamesh.platform.adapter.validator.opa.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.opendatamesh.platform.adapter.validator.opa.server.opaclient.OpaClient;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.EvaluationRequestBody;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.validator.PolicyEvaluationRequestRes;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.validator.PolicyEvaluationResultRes;
import org.opendatamesh.platform.adapter.validator.opa.server.rest.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EvaluationService {

    @Autowired
    private OpaClient opaClient;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public PolicyEvaluationResultRes validatePolicy(PolicyEvaluationRequestRes policyEvaluationRequest) {
        validatePolicyRequest(policyEvaluationRequest);
        String path = extractPackagePath(policyEvaluationRequest.getPolicy().getRawContent());
        if (!StringUtils.hasText(path)) {
            throw new BadRequestException("The policy is missing the package inside is body.");
        }
        String creationPath = path.substring(path.lastIndexOf('/') + 1);
        try {
            logger.info("Creating policy at: {}", creationPath);
            opaClient.createPolicy(creationPath, policyEvaluationRequest.getPolicy().getRawContent());

            EvaluationRequestBody evaluationRequest = new EvaluationRequestBody();
            evaluationRequest.setInput(policyEvaluationRequest.getObjectToEvaluate());

            logger.info("Validating policy at: {}", path);
            JsonNode opaResult = opaClient.validatePolicy(path, evaluationRequest, policyEvaluationRequest.getVerbose());
            logger.info("Policy: {}, validation result: {}", policyEvaluationRequest.getPolicy().getName(), new ObjectMapper().writeValueAsString(opaResult));
            return buildEvaluationResult(policyEvaluationRequest, opaResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            PolicyEvaluationResultRes policyEvaluationResult = new PolicyEvaluationResultRes();
            policyEvaluationResult.setEvaluationResult(false);
            policyEvaluationResult.setOutputObject(Map.of("error", e.getMessage()));
            return policyEvaluationResult;
        } finally {
            opaClient.deletePolicy(creationPath);
        }
    }

    private PolicyEvaluationResultRes buildEvaluationResult(PolicyEvaluationRequestRes policyEvaluationRequest, JsonNode opaResult) throws JsonProcessingException {
        PolicyEvaluationResultRes policyEvaluationResult = new PolicyEvaluationResultRes();
        policyEvaluationResult.setPolicyEvaluationId(policyEvaluationRequest.getPolicyEvaluationId());
        policyEvaluationResult.setEvaluationResult(findAllowKey(opaResult));


        policyEvaluationResult.setOutputObject(opaResult);
        return policyEvaluationResult;
    }

    private String extractPackagePath(String policyContent) {
        Pattern pattern = Pattern.compile("(?m)^package\\s+([a-zA-Z0-9_.]+)");
        Matcher matcher = pattern.matcher(policyContent);
        if (matcher.find()) {
            return matcher.group(1).replace('.', '/');
        }
        return null;
    }

    private void validatePolicyRequest(PolicyEvaluationRequestRes evaluationRequest) {
        if (evaluationRequest.getPolicy() == null) {
            throw new BadRequestException("Missing policy on evaluation request");
        }
        if (ObjectUtils.isEmpty(evaluationRequest.getObjectToEvaluate())) {
            throw new BadRequestException("Missing object to evaluate on evaluation request");
        }
    }

    private boolean findAllowKey(JsonNode node) {
        if (node.has("result") && node.get("result").isObject()) {
            if (node.get("result").has("allow") && node.get("result").get("allow").isBoolean()) {
                return node.get("result").get("allow").asBoolean(false);
            }
        }
        return false;
    }
}
