# Validation Service

This is a [SGoV validator](https://github.com/kbss-cvut/sgov-validator)-based validation service. It is a
minimalistic [Quarkus](https://quarkus.io/) application that allows invoking the validator via a REST API.

## Configuration

The following configuration properties are available:

| Property                  | Description                                       | Required |
|---------------------------|---------------------------------------------------|----------|
| `validator.repositoryUrl` | URL of the repository containing data to validate | Yes      |
| `validator.username`      | Username to connect to the repository             | No       |
| `validator.password`      | Password to connect to the repository             | No       |

## License

Licensed under GPL v3.0.
