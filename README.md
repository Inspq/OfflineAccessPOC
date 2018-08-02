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
	
### Prerequisites for the demo
* RH SSO 7.2.3 is up & running. For instructions, refer to [RH SSO Installation Guide](https://access.redhat.com/documentation/en-us/red_hat_single_sign-on/7.2/html/server_installation_and_configuration_guide/installation#installing_rh_sso_from_a_zip_file). 
	
* Spring Boot API emr-service is up & running. For instructions, refer to [EMR API readme](emr-service/README.md)

* Spring Boot Web Application emr-web is up & running. For instructions, refer to [EMR Web App readme](emr-web/README.md)
	
### Demo of offline access implementation in RH SSO
Follow the below steps for the RH SSO offline access demo
* Create a new realm **demo** by importing demo-realm.json in [RH SSO Administration console](http://localhost:8080/auth) 

* To access the Spring Boot web application, open the following link in your browser `http://localhost:8083/`

* This will display the web application landing page. Click on the link **My Publications**

* If you are not logged in, you will be redirected to the RH SSO login page

* Use the following user credentials **demo/demo** to login 
  
* Upon successful authentication, you will be presented with the list of publications returned by the emr-service

* Click on the **Logout** link to logout the user session

* Verify that the user session has been invalidated using the [RH SSO admin console](http://localhost:8080/auth)

* Use the following link to test the offline token `http://localhost:8083/offline`

* This url will make use of the offline token from the file store in order to generate a new access token. This newly generated access token will be passed as a bearer in the request header to make the REST call to the emr-service in order fetch the list of publications

* If you are able to successfully see the list of publications, you were able to successfully test the offline access implementation
		

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
	
The playbook need a Keycloak user/password to create the client and retrieve his client Secret.