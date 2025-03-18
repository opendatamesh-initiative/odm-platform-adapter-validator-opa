package org.opendatamesh.platform.adapter.validator.opa.server.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.validator.PolicyEvaluationRequestRes;
import org.opendatamesh.platform.adapter.validator.opa.server.resources.validator.PolicyEvaluationResultRes;
import org.opendatamesh.platform.adapter.validator.opa.server.services.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/evaluate-policy")
@Tag(
        name = "Policies evaluation API",
        description = "API to evaluate one policy for a given object"
)
public class ValidatorController {

    @Autowired
    private EvaluationService evaluationService;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Evaluate an object",
            description = "Evaluate an object against the provided policy",
            tags = {"Policies evaluation API"}
    )
    public PolicyEvaluationResultRes evaluate(
            @Parameter(description = "JSON object containing the object to be evaluated and the policy to validate against")
            @Valid @RequestBody PolicyEvaluationRequestRes policyEvaluationRequest,
            @RequestParam(required = false, defaultValue = "false") boolean verbose
    ) {
        return evaluationService.validatePolicy(policyEvaluationRequest, verbose);
    }


}
