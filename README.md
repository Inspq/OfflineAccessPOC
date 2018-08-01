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

`http://localhost:8082/swagger-ui.html`

### Access the Spring Boot Actuator endpoints

To access the Spring Boot actuator endpoints, open the following link in your browser:

health 
`http://localhost:8081/actuator/health`

info
`http://localhost:8081/actuator/info`
	
## Docker/Ansible
It is possible to deploy the application as a Docker container using Ansible.

### Pre-req

To be able to execute the Ansible playbook, the INSPQ Keycloak modules must be used. 

First, clone the Github repository:

	git clone https://github.com/Inspq/ansible.git
	
Checkout the inspq-2.4.01 branch

	cd ansible
	git checkout inspq-2.4.2.0-1

Source the Python Env

	source hacking/env-setup	

Docker must be installed on the machine and your user must be in the docker group (/etc/group)

Keycloak should be running on port 18081
The keycloak admin user should be admin
The admin password should be admin

### Build the Docker image

To build the docker image, execute the following commands for the root directory of the project. Those steps must be done after the app have been build with maven (mvn clean install).

	# Read artifact version
	VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec)
	# Build the Docker image
	docker build --build-arg APP_VERSION=${VERSION} -t nexus3.inspq.qc.ca:5000/inspq/emr-service:${VERSION} -t nexus3.inspq.qc.ca:5000/inspq/emr-service:latest emr-service
	docker build --build-arg APP_VERSION=${VERSION} -t nexus3.inspq.qc.ca:5000/inspq/emr-web:${VERSION} -t nexus3.inspq.qc.ca:5000/inspq/emr-web:latest emr-web
	
### Deploy a container from Docker Image

Execute the deploy.yml playbook using this command:

	ansible-playbook -i LOCAL/LOCAL.hosts deploy.yml
	
It is possible to override the defaults Keycloak port, admin user and admin password using the following command:

	ansible-playbook -i LOCAL/LOCAL.hosts -e keycloak_url=http://hostname:newport -e keycloak_user=newuser -e keycloak_password=newPassword deploy.yml
	
The playbook need a Keycloak user/password to create the client et retrieve his client Secret.