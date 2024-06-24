package main
import rego.v1

deny contains msg if {
  allowed_regions :=  ["us-west-1"]
  resource := input.resource_changes[i]
  location := resource.change.after.location
  not location in allowed_regions
  msg := sprintf("Resource %v is in an invalid region: %v", [resource.address, location])
}

