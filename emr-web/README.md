## Overview

A Simple Spring Boot Web Application to demonstrate offline access implementation of RH SSO (Keycloak)
This application is secured using RH SSO and makes a REST call to the Spring Boot microservice emr-service

## How do I use it?

### Prerequisites

- Java 8
- Apache Maven

### Build the application using Maven

`mvn clean install`

### Run the application

Navigate to the `emr-web` directory, then run:

`java -jar target/emr-web-1.0.0-SNAPSHOT.jar`

#### Alternative

`mvn clean spring-boot:run`

### Running the Tests

Unit tests will be executed during the `test` lifecycle phase and will run as part of any maven goal after `test`.

`mvn package`
