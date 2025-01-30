<!-- TOC -->

* [Open Data Mesh Adapter Validator OPA](#open-data-mesh-adapter-validator-opa)
* [How It Works](#how-it-works)
    * [How the Policy Code Should Be Structured](#how-the-policy-code-should-be-structured)
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
java -jar opa-policy-server/target/odm-platform-adapter-validator-opa-1.0.0.jar
```

*Note: The application need a reachable OPA server listening on port 8181 to correctly work. See the
section ["Run OPA server"](#run-opa-server) on how to run an OPA server with Docker.*

### Stop application

To stop the application type CTRL+C or just close the shell. To start it again re-execute the following command:

```bash
java -jar opa-policy-server/target/odm-platform-adapter-validator-opa-1.0.0.jar
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