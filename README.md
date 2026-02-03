<!-- TOC -->

* [Open Data Mesh Adapter Validator OPA](#open-data-mesh-adapter-validator-opa)
* [How It Works](#how-it-works)
    * [How the Policy Code Should Be Structured](#how-the-policy-code-should-be-structured)
    * [How Policy Evaluation Explanation Could be Logged](#how-policy-evaluation-explanation-could-be-logged)
      * [Verbose Flag](#how-explanation-is-enabled-in-the-adapter)
      * [Explanation Level (Configuration)](#loggin-level-configuration)
      * [Practical Examples](#logging-levels--practical-examples)
* [Run it](#run-it)
    * [Prerequisites](#prerequisites)
        * [Compile dependencies](#compile-dependencies)
    * [Run locally](#run-locally)
        * [Clone repository](#clone-repository)
        * [Compile project](#compile-project)
        * [Run application](#run-application)
        * [Stop application](#stop-application)
    * [Run with Docker](#run-with-docker)
        * [Clone repository](#clone-repository-1)
        * [Compile project](#compile-project-1)
        * [Run OPA server](#run-opa-server)
        * [Build image](#build-image)
        * [Run application](#run-application-1)
        * [Stop application](#stop-application-1)
    * [Run with Docker Compose](#run-with-docker-compose)
        * [Run application](#run-application-2)
        * [Stop application](#stop-application-2)
* [Test it](#test-it)
    * [REST services](#rest-services)
    * [OPA server](#opa-server)

<!-- TOC -->

# Open Data Mesh Adapter Validator OPA

[![Build](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/workflows/odm-platform-adapter-validator-opa%20CI/badge.svg)](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/actions) [![Release](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/workflows/odm-platform-adapter-validator-opa%20CI%2FCD/badge.svg)](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/actions)

Open Data Mesh Platform is a platform that manages the full lifecycle of a data product from deployment to retirement.
It uses the [Data Product Descriptor Specification](https://dpds.opendatamesh.org/) to create, deploy and operate data
product containers in a mesh architecture.

This repository contains an Adapter for [OPA](https://www.openpolicyagent.org/) (i.e., Open Policy Agent)
of the Policy Engine API service on the ODM Utility Plane.

# How It Works

The policy evaluation process is composed of these steps:

1. **Registration of a New Policy on the OPA Server:**
    - The package name is extracted from the policy code, it is mandatory and must be unique.
    - The URL to store the policy is composed based on this package name.
    - For example, if the policy has `package org.opendatamesh`, a new policy is created with:
      ```
      PUT <...>/v1/policies/org/opendatamesh
      ```

2. **Policy Validation:**
    - The policy is validated using the evaluation request content.
    - The input object passed for validation has this structure:
      ```json
      {
      "input": {
       // The policy event content (varies based on the event, but always includes currentState/afterState)
      }
      }
      ```
    - An example of a validation call can be:
       ```
       POST <...>/v1/data/org/opendatamesh
       ```
    - The supported structure of the validation output is:
      ```json
       {
         "decision_id":"403573d5-4130-4303-bd9f-2686dcb1ab5e",
         "result": {
             "allow":false,
             "otherAttribute": "another attribute of the response"
         }
       }
      ```
      where the `allow` attribute represents the evaluation result of the policy.
4. **Result Collection and Policy Removal:**
   The result is collected, and the policy is then removed from the OPA server.

## How the Policy Code Should Be Structured

Here is a policy example that satisfies this requirement:

```rego
package org.opendatamesh

default allow := false
default warning := false

allow := true {
    startswith(input.afterState.dataProductVersion.info.fullyQualifiedName, "urn")
}
```
## How Policy Evaluation Explanation Could be Logged
Open Policy Agent (OPA) supports the optional `explain` query parameter to control policy evaluation explanations and the verbosity of the evaluation trace.

The OPA Data API is the following:
```url
POST /v1/data/{package}/{rule}?explain={level}
```

The explain parameter determines how much detail OPA returns about the policy evaluation process, ranging from minimal trace messages to a complete, low-level execution trace.

The Adapter does not always enable explanations by default.
Instead, it controls their usage through two distinct and complementary mechanisms:

- a runtime flag that enables or disables explanations for a specific evaluation request

- a configuration-driven logging level that defines the verbosity of the explanation when enabled

### How explanation is enabled in the Adapter

The adapter uses **two distinct parameters**, each with a clear responsibility to enable explanations:

#### 1. `verbose` flag (runtime, per request)

The `verbose` flag is part of the policy evaluation request:

```java
public class PolicyEvaluationRequestRes {
    private Long policyEvaluationId;
    private PolicyResource policy;
    private JsonNode objectToEvaluate;
    private Boolean verbose;
}
```
When verbose is true the adapter enables explanations by adding the explain parameter to the OPA Data API request.

If verbose is false the adapter does not include the explain parameter in the OPA call

#### 2. OPA logging level (configuration)

When verbose = true, the adapter invokes OPA with a specific explanation level.
- The explanation level is not decided by the API caller
- the value of explain is taken from the configured opa.loggingLevel
- The value is mapped directly to OPA’s explain query parameter

If no logging level is explicitly configured, the adapter defaults to:

```url
explain=notes
```
### Loggin Level Configuration

The logging (explain) level is configured through the OPA configuration section:

```yaml
##########################
# OPA configuration
##########################
opa:
  url:
    loggingLevel: "notes|debug|full"
```  

The value of loggingLevel is directly mapped to the OPA explain query parameter used when evaluating policies.

| Level | Purpose |
|-------|---------|
| `notes` | Minimal, shows only messages explicitly emitted by the policy via `trace()` |
| `debug` | Developer-oriented, shows rule evaluation flow and includes `trace()` notes |
| `full` | Maximum verbosity, includes every evaluation step (rarely needed) |

## Logging Levels – Practical Examples

This section shows how different OPA `explain` levels affect the evaluation output using the **same policy and input**.

The examples below are produced by evaluating the following policy, which validates the input structure by ensuring that all fields define a non-empty type.
The policy fails when at least one field is missing its type and reports each violation accordingly.

```rego
package dataproduct

import future.keywords.in

default allow := false

missing_field[f.name] {
    some f in input.fields
    f.type == ""
    trace(sprintf("Missing type for field: %v", [f.name]), true)
}

missing_field[f.name] {
    some f in input.fields
    f.type == null
    trace(sprintf("Missing type for field: %v", [f.name]), true)
}

allow {
    count(missing_field) == 0
}
```

Input Example
```json
{
  "input": {
    "fields": [
      {
        "name": "age",
        "type": "int"
      },
      {
        "name": "salary",
        "type": ""
      },
      {
        "name": "isActive",
        "type": null
      }
    ]
  }
}
```

### Result with **explain=notes**

The notes level returns only messages explicitly emitted by the policy via the trace() built-in.
```json
{
  "policyEvaluationId": 1,
  "evaluationResult": false,
  "outputObject": {
    "decision_id": "4efb727f-1fa9-4da3-a19b-377d5d2216a7",
    "explanation": [
            "query:1 Enter data.dataproduct = _",
            "dataproduct:19 | Enter data.dataproduct.allow",
            "dataproduct:7  | | Enter data.dataproduct.missing_field",
            "dataproduct:10 | | | Note \"Missing type for field: salary\"",
            "dataproduct:13 | | Enter data.dataproduct.missing_field",
            "dataproduct:16 | | | Note \"Missing type for field: isActive\""
          ],
    "result": {
        "allow": false,
        "missing_field": [
            "isActive",
            "salary"
            ]
        }
    }
}
```
What is happening

- Each failing rule explicitly calls trace()
- Each trace() generates a Note event
- explain=notes shows only those Note events

Lines such as:
```kotlin
Enter data.dataproduct.allow
Enter data.dataproduct.missing_field
```

are structural markers added by OPA to show which rule is being evaluated.

They are minimal compared to debug or full, but help contextualize the Note messages.

#### Result with explain=notes (WITHOUT trace())

If the same policy logic is evaluated without using trace(), the notes logging level has no messages to display.

Example output:
```json
{
  "policyEvaluationId": 1,
  "evaluationResult": false,
  "outputObject": {
    "decision_id": "aae6b691-bafd-4237-951a-f69fad063ad9",
    "explanation": [
      ""
    ],
    "result": {
      "allow": false,
      "missing_field": [
        "isActive",
        "salary"
      ]
    }
  }
}
```
This behavior is expected and by design.
- explain=notes does not automatically describe rule evaluation
- It only displays messages explicitly emitted via trace()
- If no trace() calls are executed, OPA has nothing to report
  
OPA does **not** invent explanations.

### Result with explain=debug

The debug level provides a developer-oriented explanation, showing how rules and expressions are evaluated, while still including trace() notes.
```json
{
  "policyEvaluationId": 1,
  "evaluationResult": false,
  "outputObject": {
    "decision_id": "a860a547-f3bd-4652-98b5-14fa090df499",
    "explanation": [
      "query:1            Enter data.dataproduct = _",
      "query:1            | Eval data.dataproduct = _",
      "query:1            | Unify data.dataproduct = _",
      "query:1            | Unify data.dataproduct.missing_field = _",
      "query:1            | Index data.dataproduct.allow (matched 1 rule, early exit)",
      "dataproduct:17     | Enter data.dataproduct.allow",
      "dataproduct:18     | | Eval __local9__ = data.dataproduct.missing_field",
      "dataproduct:18     | | Unify __local9__ = data.dataproduct.missing_field",
      "dataproduct:18     | | Index data.dataproduct.missing_field (matched 2 rules)",
      "dataproduct:7      | | Enter data.dataproduct.missing_field",
      "dataproduct:8      | | | Eval f = input.fields[__local0__]",
      "dataproduct:8      | | | Unify f = input.fields[__local0__]",
      "dataproduct:8      | | | Unify 0 = __local0__",
      "dataproduct:8      | | | Unify {\"name\": \"age\", \"type\": \"int\"} = f",
      "dataproduct:9      | | | Eval f.type = \"\"",
      "dataproduct:9      | | | Unify f.type = \"\"",
      "dataproduct:9      | | | Unify \"int\" = \"\"",
      "dataproduct:9      | | | Fail f.type = \"\"",
      "dataproduct:8      | | | Redo f = input.fields[__local0__]",
      "dataproduct:8      | | | Unify 1 = __local0__",
      "dataproduct:8      | | | Unify {\"name\": \"salary\", \"type\": \"\"} = f",
      "dataproduct:9      | | | Eval f.type = \"\"",
      "dataproduct:9      | | | Unify f.type = \"\"",
      "dataproduct:9      | | | Unify \"\" = \"\"",
      "dataproduct:7      | | | Eval __local7__ = f.name",
      "dataproduct:7      | | | Unify __local7__ = f.name",
      "dataproduct:7      | | | Unify \"salary\" = __local7__",
      "dataproduct:7      | | | Exit data.dataproduct.missing_field",
      "dataproduct:7      | | Redo data.dataproduct.missing_field",
      "dataproduct:7      | | | Redo __local7__ = f.name",
      "dataproduct:9      | | | Redo f.type = \"\"",
      "dataproduct:8      | | | Redo f = input.fields[__local0__]",
      "dataproduct:8      | | | Unify 2 = __local0__",
      "dataproduct:8      | | | Unify {\"name\": \"isActive\", \"type\": null} = f",
      "dataproduct:9      | | | Eval f.type = \"\"",
      "dataproduct:9      | | | Unify f.type = \"\"",
      "dataproduct:9      | | | Unify null = \"\"",
      "dataproduct:9      | | | Fail f.type = \"\"",
      "dataproduct:8      | | | Redo f = input.fields[__local0__]",
      "dataproduct:12     | | Enter data.dataproduct.missing_field",
      "dataproduct:13     | | | Eval f = input.fields[__local3__]",
      "dataproduct:13     | | | Unify f = input.fields[__local3__]",
      "dataproduct:13     | | | Unify 0 = __local3__",
      "dataproduct:13     | | | Unify {\"name\": \"age\", \"type\": \"int\"} = f",
      "dataproduct:14     | | | Eval f.type = null",
      "dataproduct:14     | | | Unify f.type = null",
      "dataproduct:14     | | | Unify \"int\" = null",
      "... many additional trace events ..."
    ],
    "result": {
      "allow": false,
      "missing_field": [
        "isActive",
        "salary"
      ]
    }
  }
}
```

The `debug` explanation level provides a **developer-oriented view** of how OPA evaluates a policy.

It includes:

- Rule entry and exit
- Rule and expression evaluation flow
- Key unification steps
- Evaluation failures and retries
- All messages emitted via the `trace()` built-in

The information returned by `debug` is a **filtered subset of the `full` explanation level**.
It focuses on the logical execution path that leads to the final decision, while omitting low-level internal engine details such as exhaustive backtracking and intermediate bindings.

### Result with explain=full
The `full` explanation level shows **everything OPA does** during policy evaluation.

It includes:
- Every unification
- Every comparison
- Complete backtracking
- Temporary variable bindings
- Failed evaluation attempts

It also includes evaluation paths that **do not lead to any final result**.

This level provides a raw, low-level view of the OPA evaluation engine, with no filtering or simplification.

# Run it

## Prerequisites

The project requires the following dependencies:

* Java 11
* Maven 3.8.6
* [OPA Rootless](https://hub.docker.com/layers/openpolicyagent/opa/latest-rootless/images/sha256-b8d2ca87f0241531433d106473bbe3661b7c9be735c447daefa164f2c3942b8d?context=explore)

### Compile dependencies

Compile the project:

```bash
mvn clean install -DskipTests
```

## Run locally

### Clone repository

Clone the repository and move to the project root folder

```bash
git git clone https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa.git
cd odm-platform-adapter-validator-opa
```

### Compile project

Compile the project:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Run application

Run the application:

```bash
java -jar opa-policy-server/target/odm-platform-adapter-validator-opa-1.0.1.jar
```

*Note: The application need a reachable OPA server listening on port 8181 to correctly work. See the
section ["Run OPA server"](#run-opa-server) on how to run an OPA server with Docker.*

### Stop application

To stop the application type CTRL+C or just close the shell. To start it again re-execute the following command:

```bash
java -jar opa-policy-server/target/odm-platform-adapter-validator-opa-1.0.1.jar
```

## Run with Docker

*_Dependencies must have been compiled to run this project._

### Clone repository

Clone the repository and move it to the project root folder

```bash
git git clone https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa.git
cd odm-platform-adapter-validator-opa
```

Here you can find the Dockerfile which creates an image containing the application by directly copying it from the build
executed locally (i.e. from `target` folder).

### Compile project

You need to first execute the build locally by running the following command:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Run OPA server

The image generated from Dockerfile contains only the application. It requires an OPA server to run properly. If you do
not already have an OPA server available, you can create one by running the following commands:

```bash
docker run --name odm-opa-server -d -p 8181:8181  \
   openpolicyagent/opa:latest-rootless \
   run \
   --server \
   --log-level=debug  \
   --log-format=json-pretty \
   --set=decision_logs.console=true
```

Check that the OPA server has started correctly:

```bash
docker logs odm-opa-server
```

### Build image

Build the Docker image of the application and run it.

```bash
docker build -t odm-platform-up-validator-opa-server-app . -f Dockerfile 
```

### Run application

Run the Docker image.

*Note: Before executing the following commands remove the argument `--net host` if OPA server is not running
on `localhost`*

```bash
docker run --name odm-platform-up-validator-opa-server-app -p 9009:9009 --net host odm-platform-up-validator-opa-server-app
```

### Stop application

```bash
docker stop odm-platform-up-validator-opa-server-app
docker stop odm-opa-server
```

To restart a stopped application execute the following commands:

```bash
docker start odm-opa-server
docker start odm-platform-up-validator-opa-server-app
```

To remove a stopped application to rebuild it from scratch execute the following commands :

```bash
docker rm odm-platform-up-validator-opa-server-app
docker rm odm-opa-server
```

## Run with Docker Compose

Create a `.env` file in the root directory of the project similar to the following one:

```.dotenv
OPA_PORT=8181
SPRING_PORT=9009
OPA_HOSTNAME="opa"
```

### Run application

Run the docker-compose images.

```bash
docker-compose up
```

### Stop application

Stop the docker-compose images

```bash
docker-compose down
```

To restart a stopped application execute the following commands:

```bash
docker-compose up
```

# Test it

## REST services

You can invoke REST endpoints through *OpenAPI UI* available at the following url:

* [http://localhost:9009/api/v1/up/validator/swagger-ui/index.html](http://localhost:9009/api/v1/up/validator/swagger-ui/index.html)

## OPA server

You can access to OPA Server browsing tho the following page:

* [http://localhost:8181/](http://localhost:8181/)
