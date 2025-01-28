package org.opendatamesh.platform.adapter.validator.opa.server.resources.policy;

import java.util.List;

public class PolicyResource {
    private Long id;
    private Long rootId;
    private String name;
    private String displayName;
    private String description;
    private Boolean blockingFlag;
    private String rawContent;
    private String suite;
    private List<PolicyEvaluationEventResource> evaluationEvents;
    private String filteringExpression;
    private Boolean isLastVersion;
    private PolicyEngineResource policyEngine;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getBlockingFlag() {
        return blockingFlag;
    }

    public void setBlockingFlag(Boolean blockingFlag) {
        this.blockingFlag = blockingFlag;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getFilteringExpression() {
        return filteringExpression;
    }

    public void setFilteringExpression(String filteringExpression) {
        this.filteringExpression = filteringExpression;
    }

    public Boolean getLastVersion() {
        return isLastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        isLastVersion = lastVersion;
    }

    public PolicyEngineResource getPolicyEngine() {
        return policyEngine;
    }

    public void setPolicyEngine(PolicyEngineResource policyEngine) {
        this.policyEngine = policyEngine;
    }

    public List<PolicyEvaluationEventResource> getEvaluationEvents() {
        return evaluationEvents;
    }

    public void setEvaluationEvents(List<PolicyEvaluationEventResource> evaluationEvents) {
        this.evaluationEvents = evaluationEvents;
    }
}


