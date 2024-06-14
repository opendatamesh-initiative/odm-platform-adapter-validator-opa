# Terraform Plan Validation Use Cases

This folder contains examples of Open Policy Agent (OPA) policies used to validate Terraform plans across
various scenarios. Each example is made of a `.rego` file that contains the policy and a `.json` file that contains a
Terraform plan output or a part of it (this has been converted in JSON format using the `terraform show` command).

## Contents

- `/region-validation-simple-case`: In the Terraform plan, the region is declared inside the provider configuration and
  is the same for all the resources. The policy enforces that the region remains the same as the one that has been
  designated. This ensures consistency and compliance to organization regulations.
- `/region-validation-complex-case`: In the Terraform plan, each resource can be deployed in a different region. The
  policy enforces that the regions remain the same as the ones that have been designated. This ensures consistency and
  compliance to organization regulations.
- `/resource-tag-validation`: The policy enforces the presence of tags in each resource, avoiding untagged ones. This
  helps with resource management, tracking, and cost allocation.
- `/security-group-rule-validation`: The policy checks that every security group does not have an inbound rule that
  allows the ingress of all IPs [0.0.0.0/0]. This helps to prevent potential security threats by limiting access to
  resources.

## How to Validate a Terraform Plan

Before you begin, ensure the following components are up and running:

- ODM Platform with:
  - ODM Registry
  - ODM Notification
  - ODM DevOps
  - ODM Policy
- `validator-opa` (the service from this repository)
- An instance of the OPA (Open Policy Agent) server, to which `validator-opa` will connect

Follow these steps:

1. Save the policies on the ODM platform.
2. Configure the DevOps activity so the Terraform plan can be validated with the previously stored policies.

   **Note:** The Terraform plan output must first be converted to JSON format using the `terraform show` command to be evaluated by the OPA engine.

## Contributing

Contributions to this repository are welcome. Please feel free to open a pull request or issue if you have any
improvements or suggestions. When contributing, please make sure to update the examples and documentation accordingly.