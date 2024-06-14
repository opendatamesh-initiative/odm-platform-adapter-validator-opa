package main
import rego.v1

deny contains msg if {
	allowed_regions :=  ["us-west-1"]
	provider_regions := {provider: region |
    	provider := input.configuration.provider_config[_].name
    	region := input.configuration.provider_config[_].expressions.region.constant_value
	}
    some provider
    region := provider_regions[provider]
    not region in allowed_regions
    msg := sprintf("The provider %v is configured in an invalid region: %v", [provider, region])
}
