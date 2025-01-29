package org.opendatamesh.platform.adapter.validator.opa.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.opendatamesh.platform.adapter.validator.opa.server.opaclient.OpaClient;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.EvaluationRequestBody;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.EvaluationRequestResponse;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.validator.PolicyEvaluationRequestRes;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.validator.PolicyEvaluationResultRes;
import org.opendatamesh.platform.adapter.validator.opa.server.rest.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        if(!StringUtils.hasText(path)){
            throw new BadRequestException("The policy is missing the package inside is body.");
        }
        String creationPath = path.substring(path.lastIndexOf('/') + 1);
        String validationPath = path + "/allow";
        try {
            logger.info("Creating policy at: {}", creationPath);
            opaClient.createPolicy(creationPath, policyEvaluationRequest.getPolicy().getRawContent());

            EvaluationRequestBody evaluationRequest = new EvaluationRequestBody();
            evaluationRequest.setInput(policyEvaluationRequest.getObjectToEvaluate());

            logger.info("Validating policy at: {}", path);
            EvaluationRequestResponse opaResult = opaClient.validatePolicy(validationPath, evaluationRequest);
            logger.info("Policy: {}, validation result: {}", policyEvaluationRequest.getPolicy().getName(), new ObjectMapper().writeValueAsString(opaResult.getResult()));
            return buildEvaluationResult(policyEvaluationRequest, opaResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            PolicyEvaluationResultRes policyEvaluationResult = new PolicyEvaluationResultRes();
            policyEvaluationResult.setEvaluationResult(false);
            PolicyEvaluationResultRes.OutputObject outputObject = new PolicyEvaluationResultRes.OutputObject();
            outputObject.setMessage(e.getMessage());
            policyEvaluationResult.setOutputObject(outputObject);
            return policyEvaluationResult;
        } finally {
            opaClient.deletePolicy(creationPath);
        }
    }

    private PolicyEvaluationResultRes buildEvaluationResult(PolicyEvaluationRequestRes policyEvaluationRequest, EvaluationRequestResponse opaResult) throws JsonProcessingException {
        PolicyEvaluationResultRes policyEvaluationResult = new PolicyEvaluationResultRes();
        policyEvaluationResult.setPolicyEvaluationId(policyEvaluationRequest.getPolicyEvaluationId());

        if (opaResult.getResult() != null && opaResult.getResult().isBoolean()) {
            policyEvaluationResult.setEvaluationResult(opaResult.getResult().asBoolean(false));
        } else {
            policyEvaluationResult.setEvaluationResult(false);
        }

        PolicyEvaluationResultRes.OutputObject outputObject = new PolicyEvaluationResultRes.OutputObject();
        outputObject.setMessage(new ObjectMapper().writeValueAsString(opaResult));
        policyEvaluationResult.setOutputObject(outputObject);
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
}
