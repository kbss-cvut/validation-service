# Validation Service

This is a [SGoV validator](https://github.com/kbss-cvut/sgov-validator)-based validation service. It is a
minimalistic [Quarkus](https://quarkus.io/) application that allows invoking the validator via a REST API.

## Configuration

The following configuration properties are available:

| Property                    | Description                                                        | Required | Default value |
|-----------------------------|--------------------------------------------------------------------|----------|---------------|
| `validator.repositoryUrl`   | URL of the repository containing data to validate                  | Yes      | N/A           |
| `validator.username`        | Username to connect to the repository                              | No       | N/A           |
| `validator.password`        | Password to connect to the repository                              | No       | N/A           |
| `validator.defaultLanguage` | Default validation language in case the client does not specify it | No       | `"cs"`        |

## License

Licensed under GPL v3.0.
