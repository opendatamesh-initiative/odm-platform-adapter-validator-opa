package main
import rego.v1

deny contains msg if {
	actions := ["create", "update", "replace"]
    some i
    resource := input.resource_changes[i]
    resource.change.actions[_] in actions
    not resource.change.after.tags
    msg := sprintf("The resource %v is missing tags", [resource.address]) 
}
