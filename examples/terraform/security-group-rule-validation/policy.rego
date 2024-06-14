package main
import rego.v1

deny contains msg if {
	actions := ["create", "update", "replace"]
    some i
    resource := input.resource_changes[i]
    resource.change.actions[_] in actions
    some l
    ingress := resource.change.after.ingress[l]
    some j
    ingress.cidr_blocks[j] == "0.0.0.0/0"
    msg := sprintf("The resource %v is violating the security constraint of allowing  unrestricted ingress from 0.0.0.0/0", [resource.address]) 
}
