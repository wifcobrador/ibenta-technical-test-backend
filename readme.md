 # Ibenta Technical Test (Backend)

Welcome to the Ibenta Technical Test for Backend Developers. Please make sure you have a GitHub account and you have
a write access to this repo.

#### To do this test:
1. Clone this repo
1. Create a branch: `feature/<Your Name in Kebab Case>-<Todays Date>` e.g. `feature/john-smith-25-09-2020`
1. Push all your changes in that branch.
1. Create a PR. This will run the build and creates a [preview environment](https://jenkins-x.io/docs/build-test-preview/preview)
1. Notify the parties involved and send a link to your PR.

#### Junior Level:
1. Create a new class `UserService` under `au.com.ibenta.test.service`.
1. Implement the following methods with the following specifications:
    1. `create` - Creates a new user and saves it in the database.
    1. `get` - Get an existing user from the database by ID.
    1. `update` - Update an existing user with the provided details.
    1. `delete` - Deletes an existing user.
    1. `list` - Lists all users.

Bonus: If you can write tests for this service please do so.

---
#### Mid Level:    
1. Do all the steps in the `Junior Level`
1. Create rest controller `UserController` under `au.com.ibenta.test.service`.
1. Implement the following methods with the following specifications: (Please try to define the API endpoints accordingly following Standard ReST)
    1. `create` - Exposes the `create` method from `UserService`. Returns HTTP 201 and the UserEntity on success.
    1. `get` - Exposes the `get` method from `UserService`. Returns HTTP 200 and the UserEntity on success.
    1. `update` - Exposes the `update` method from `UserService`. Returns HTTP 200 and the updated UserEntity on success.
    1. `delete` - Exposes the `delete` method from `UserService`. Returns 201 with empty body on success.
    1. `list` - Exposes the `list` method from `UserService`. Returns 200 with an array of all the users.

Bonus: If you can write tests please do so.

---    
#### Senior Level:
1. Do all the steps in the `Junior Level` & `Mid Level`
1. Create a DTO instead of returning the `UserEntity` directly. Name the DTO `User` and put it under `au.com.ibenta.test.model`
1. Add some validations:
    1. `firstName` is required
    1. `lastName` is required
    1. `email` is required and should be a proper email format
    1. `password` is required and should be encoded before saving in the database. This field should also be omitted when returning from the API.
1. Create an endpoint that will return the response from this URL. `http://authentication-service.staging.ibenta.com/actuator/health`.
1. Write unit/integration to cover as much as you can.

## Requirements:
- GitHub Account
- Java 11
- Maven
---
## Running the app
```
mvn spring-boot:run
```

## Running template tests & build
```
mvn clean test
mvn clean install
```
---

## Endpoints used by Kubernetes

This quickstart exposes the following endpoints important for Kubernetes deployments:
- `/actuator/health` - Spring Boot endpoint indicating health. Used by Kubernetes as readiness probe.
- `/actuator/metrics` - Prometheus metrics. Invoked periodically and collected by Prometheus Kubernetes scraper.
