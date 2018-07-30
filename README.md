Offline Access POC
=========

This project is a POC to demonstrate how to work with Keycloak's offline token.
Here are the steps to demonstrate:

	1) User access the web UI
	2) He is redirected to Keycloak to authenticate.
	3) He do the authentication.
	4) He is redirected to the web UI.
	5) He request an offline token
	6) The application persists the token for 24 hours.
	7) a couple of hours later, the request is sent to the API using the offline token
	8) We can see in the log that the request have been done using the offline token.

Requirements
------------

here are the requirements:

	A Keycloak server where we will create clients for a web UI and an REST API
	Web UI secured by Keycloak a user can use to call the REST API
	A Springboot REST API secured by Keycloak Springboot adapter.
	A username and password in Keycloak to log in the application. 
	
	
## Overview

A Simple Spring Boot REST API to demonstrate offline access implementation of RH SSO (Keycloak) 

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

`mvn spring-boot:run`

### Running the Tests

Unit tests will be executed during the `test` lifecycle phase and will run as part of any maven goal after `test`.

`mvn package`

### Access the application

To access the application, open the following link in your browser:

`http://localhost:8080/swagger-ui.html`

### Access the Spring Boot Actuator endpoints

To access the Spring Boot actuator endpoints, open the following link in your browser:

health 
`http://localhost:8081/actuator/health`

info
`http://localhost:8081/actuator/info`
	