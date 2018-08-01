## Overview

A Simple Spring Boot REST API to demonstrate offline access implementation of RH SSO (Keycloak)
This Spring Boot microservice emr-service is secured using RH SSO and consumed by the Spring Boot Web Application emr-web

## How do I use it?

### Prerequisites

- Java 8
- Apache Maven

### Build the application using Maven

`mvn clean install`

### Run the application

Navigate to the `emr-service` directory, then run:

`java -jar target/emr-service-1.0.0-SNAPSHOT.jar`

#### Alternative

`mvn clean spring-boot:run`

### Running the Tests

Unit tests will be executed during the `test` lifecycle phase and will run as part of any maven goal after `test`.

`mvn package`

### Access the application

To access the application, open the following link in your browser:

`http://localhost:8082/swagger-ui.html`

### Access the Spring Boot Actuator endpoints

To access the Spring Boot actuator endpoints, open the following link in your browser:

health 
`http://localhost:8081/actuator/health`

info
`http://localhost:8081/actuator/info`