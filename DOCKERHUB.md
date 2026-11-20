# ODM Platform Validator Adapter for OPA

The Validator Adapter for OPA is the [Open Policy Agent](https://www.openpolicyagent.org/) policy engine of [Open Data Mesh Platform](https://github.com/opendatamesh-initiative). The ODM Policy service delegates the evaluation of Rego policies to it: for each request, the adapter uploads the policy to an OPA server, evaluates the object it receives against it, returns the result, and removes the policy from OPA. The image contains only the adapter, not OPA: it needs an external OPA server.

- **Source code:** [opendatamesh-initiative/odm-platform-adapter-validator-opa](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa)
- **Documentation:** [README](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/blob/main/README.md)
- **Issues:** [GitHub issues](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/issues)

## Tags

Each [GitHub release](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/releases) publishes one image tagged with its version, without the `v` prefix:

```bash
docker pull opendatamesh/odm-platform-adapter-validator-opa:<version>
```

There is no `latest` tag: always pin a version.

## Requirements

| Dependency | Required | Notes |
| --- | --- | --- |
| [OPA server](https://hub.docker.com/r/openpolicyagent/opa) | Yes | The adapter calls the OPA REST API (`/v1/policies` and `/v1/data`). It doesn't check the connection on startup. |

OPA 1.0 and later accept only Rego v1 syntax by default (`if` and `contains` keywords in rule definitions). The policy examples in the repository use the older syntax, for example `allow := true { ... }`: to evaluate policies like these, start OPA with `--v0-compatible`. OPA 1.0 and later also listen on `localhost` only by default: in a container, start OPA with `--addr=0.0.0.0:8181`.

## Quick start

Starts OPA and the adapter on the same Docker network:

```bash
docker network create odm-opa

docker run -d --name opa --network odm-opa \
  openpolicyagent/opa:latest-rootless \
  run --server --addr=0.0.0.0:8181 --v0-compatible

docker run -d --name odm-validator-opa --network odm-opa -p 9009:9009 \
  -e OPA_HOSTNAME=opa \
  opendatamesh/odm-platform-adapter-validator-opa:<version>
```

Drop `--v0-compatible` if all your policies use Rego v1 syntax. To use an OPA server that's already running, set `OPA_HOSTNAME` and `OPA_PORT` to its address.

When it's up, the API is documented at `http://localhost:9009/api/v1/up/validator/swagger-ui/index.html` (OpenAPI at `/api/v1/up/validator/v3/api-docs`).

## How it works

The adapter exposes one endpoint, `POST /api/v1/up/validator/evaluate-policy`. Register the adapter in the Policy service as a policy engine, using its base URL (for example `http://validator-opa.example.com:9009`).

The request carries the policy (its Rego source in `policy.rawContent`), the object to evaluate (`objectToEvaluate`), and an optional `verbose` flag. The adapter:

1. reads the `package` declaration from the policy, which is mandatory, and uploads the policy to OPA;
2. evaluates the package with the object as `input` (`POST /v1/data/<package path>`);
3. returns `evaluationResult`, which is the boolean `allow` rule of the package (`false` when it's missing or not a boolean), and the full OPA response as `outputObject`;
4. deletes the policy from OPA.

Policies are uploaded under the last segment of their package name, so policies evaluated at the same time need distinct package names.

The adapter doesn't report errors returned by OPA: a policy that OPA rejects, for example for a syntax error, simply evaluates to `evaluationResult: false`. If every evaluation fails, check the OPA logs. When OPA is unreachable, the request fails with HTTP 500.

With `verbose: true`, the adapter asks OPA for an explanation of the evaluation (the OPA `explain` parameter), with the level set in `opa.url.loggingLevel`. See [How Policy Evaluation Explanation Could be Logged](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/blob/main/README.md#how-policy-evaluation-explanation-could-be-logged).

## Environment variables

### Image

| Variable | Description | Default |
| --- | --- | --- |
| `OPA_HOSTNAME` | Host name of the OPA server. The adapter calls `http://<OPA_HOSTNAME>:<OPA_PORT>`. | `localhost` |
| `OPA_PORT` | Port of the OPA server. | `8181` |
| `SPRING_PORT` | HTTP port of the adapter (`server.port`). | `9009` |
| `SPRING_PROFILES_ACTIVE` | Spring profiles to activate. Keep `docker`: it's the profile that reads the three variables above. | `docker` |
| `JAVA_OPTS` | Extra JVM options, for example `-Xmx512m`. | empty |
| `SPRING_PROPS` | Extra arguments added to the `java` command line before `-jar`, such as `-Dtimeout.seconds=60`. It does not accept JSON: use `SPRING_APPLICATION_JSON` for that. | empty |

`localhost` is the container itself: unless the container runs with `--network host`, set `OPA_HOSTNAME`.

### Application

Every property can be set with `SPRING_APPLICATION_JSON`, with a `-D` argument in `SPRING_PROPS`, or as an environment variable, using Spring Boot relaxed binding (for example, `timeout.seconds` becomes `TIMEOUT_SECONDS`).

| Property | Description | Default |
| --- | --- | --- |
| `opa.url.policies` | URL of the OPA policies API. Set it, together with `opa.url.data`, to reach OPA over HTTPS or behind a path prefix. | `http://<OPA_HOSTNAME>:<OPA_PORT>/v1/policies` |
| `opa.url.data` | URL of the OPA data API. | `http://<OPA_HOSTNAME>:<OPA_PORT>/v1/data` |
| `opa.url.loggingLevel` | OPA `explain` level used when a request sets `verbose: true`: `notes`, `debug` or `full`. | `notes` |
| `timeout.seconds` | Connect and read timeout of the calls to OPA, in seconds. | `30` |

For example, to get `debug` explanations:

```bash
docker run -d --name odm-validator-opa --network odm-opa -p 9009:9009 \
  -e OPA_HOSTNAME=opa \
  -e SPRING_APPLICATION_JSON='{ "opa": { "url": { "loggingLevel": "debug" } } }' \
  opendatamesh/odm-platform-adapter-validator-opa:<version>
```

## Ports and volumes

- **Port:** `9009` (HTTP), or the value of `SPRING_PORT`. The API is under the `/api/v1/up/validator` context path.
- **Volumes:** none needed. The image declares `/tmp` as an anonymous volume, but the adapter is stateless: policies live in OPA only for the duration of an evaluation.

## Learn more

- [Policy evaluation and explanation levels](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/blob/main/README.md#how-it-works)
- [Example Rego policies for Terraform plans](https://github.com/opendatamesh-initiative/odm-platform-adapter-validator-opa/blob/main/examples/terraform/README.md)
- [OPA documentation](https://www.openpolicyagent.org/docs/)

Licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
