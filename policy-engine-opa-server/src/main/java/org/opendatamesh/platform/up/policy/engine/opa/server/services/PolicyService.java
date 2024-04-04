package org.opendatamesh.platform.up.policy.engine.opa.server.services;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;;
import org.opendatamesh.platform.up.policy.engine.opa.server.opaclient.OpaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PolicyService {

    @Autowired
    OpaClient opaClient;

    public Iterable<PolicyResource> getAllPolicies(){

        List<PolicyResource> policies = new ArrayList<>();
        Map<String, Object> opaResponse = opaClient.getPolicies();
        List<Map<String, Object>> result = (List<Map<String, Object>>) opaResponse.get("result");
        for (Map<String, Object> policy : result){
            PolicyResource policyResource = new PolicyResource();
            policyResource.setName((String) policy.get("id"));
            policyResource.setRawContent((String)policy.get("raw"));
            policies.add(policyResource);
        }
    
        return policies;
    
    }

    public PolicyResource getPolicyByID(String id){

        Map<String, Object> opaResponse = opaClient.getPolicyById(id);
        Map<String, Object> result = (Map<String, Object>) opaResponse.get("result");
        PolicyResource policyResource = new PolicyResource();
        policyResource.setName((String) result.get("id"));
        policyResource.setRawContent((String)result.get("raw"));

        return policyResource;
    }

    public void savePolicyOnOpaServer(PolicyResource policy) {
        putPolicy(policy.getName(), policy.getRawContent());
    }

    private void putPolicy(String id, String policy){
        opaClient.updatePolicy(id, policy);
    }

    public void deletePolicyFromOpaServer(String policyId) {
        deletePolicyById(policyId);
    }

    private void deletePolicyById(String id){
        opaClient.deletePolicyById(id);
    }

}
