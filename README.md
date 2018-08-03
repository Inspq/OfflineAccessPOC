Offline Access POC
=========

This project is a POC to demonstrate how to work with Keycloak's offline token.
Here are the steps to demonstrate:

	1) User access the web UI
	2) He is redirected to Keycloak to authenticate.
	3) He do the authentication.
	4) He is redirected to the web UI.
	5) The application request an offline token
	6) The application persists the token for 24 hours.
	7) After a logout, the user can access without authentication to the offline section of the web application to do a request to the API using the offline token
	8) We can see in the log that the request have been done using the offline token.

Requirements
------------

here are the requirements:

	A Keycloak server securing the web UI and the REST API
	Web UI secured by Keycloak a user can use to call the REST API
	A Springboot REST API secured by Keycloak Springboot adapter.
	A username and password in Keycloak to log in the application. 

### RH SSO considerations for Offline Access Implementation:
1. To be able to issue an offline token, users need to have the role mapping for the realm-level role offline_access. 
2. Clients also need to have offline_access role in their scope.
3. The client can request an offline token by adding the parameter scope=offline_access when sending authorization request to Red Hat Single Sign-On. 
4. The Red Hat Single Sign-On OIDC client adapter automatically adds this parameter when you use it to access secured URL of your application (i.e. http://localhost:8080/customer-portal/secured?scope=offline_access). 
5. The Direct Access Grant and Service Accounts also support offline tokens if you include scope=offline_access in the body of the authentication request.

Refer to the [RH SSO documentation](https://access.redhat.com/documentation/en-us/red_hat_single_sign-on/7.2/html-single/server_administration_guide/#offline-access) for more details

### Prerequisites for the offline access demo
* RH SSO 7.2.3 is up & running. For instructions, refer to [RH SSO Installation Guide](https://access.redhat.com/documentation/en-us/red_hat_single_sign-on/7.2/html/server_installation_and_configuration_guide/installation#installing_rh_sso_from_a_zip_file). 
	
* Spring Boot API emr-service is up & running. For instructions, refer to [EMR API readme](emr-service/README.md)

* Spring Boot Web Application emr-web is up & running. For instructions, refer to [EMR Web App readme](emr-web/README.md)

* Create a new realm **demo** by importing demo-realm.json in [RH SSO Administration console](http://localhost:8080/auth) 

* Or use the Ansible/Docker instructions below to create the keycloak, emr-service and emr-web containers. The scripts will also create the demo realm.
	
### Demo of offline access implementation in RH SSO
Follow the below steps for the RH SSO offline access demo

* To access the Spring Boot web application, open the following link in your browser `http://localhost:8083/`

* This will display the web application landing page. Click on the link **My Publications**

* If you are not logged in, you will be redirected to the RH SSO login page

* Use the following user credentials **demo/demo** to login 
  
* Upon successful authentication, you will be presented with the list of publications returned by the emr-service

* Click on the **Logout** link to logout the user session. This should take you back to the home page.

* Verify that the user session has been invalidated using the [RH SSO admin console](http://localhost:8080/auth)

* Now, you can click on the link **Offline Access to My publications** to test the offline access

* This link will call 'http://localhost:8083/offline' which will retrieve the stored offline token from the file store and generate a new access token. Once a new access token is generated, it will be passed as a bearer token in the request header to make the REST call to the emr-service in order fetch the list of publications

* If you are able to see the list of publications, you were able to successfully test the offline access implementation
		

## Docker/Ansible
It is possible to deploy the application as a Docker container using Ansible.

### Ansible configuration

To be able to execute the Ansible playbook, the INSPQ Keycloak modules must be used. 

First, clone the Github repository:

	git clone https://github.com/Inspq/ansible.git
	
Checkout the inspq-2.4.01 branch

	cd ansible
	git checkout inspq-2.4.2.0-1

Source the Python Env

	source hacking/env-setup

Docker must be installed on the machine and your user must be in the docker group (/etc/group)

### Keycloak server
You can install the Keycloak server using ansible script from the INSPQ Ansible repository using the following command:

	ansible-playbook -i keycloak.hosts deploy-keycloak.yml
	
The server will run with the following parameter:

	Exposed port for the keycloak server is 18081
	The keycloak admin user is admin
	The admin password is admin

### Build the Docker image

To build the docker image, execute the following commands for the root directory of the project. Those steps must be done after the app have been build with maven (mvn clean install).

	# Read artifact version
	VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec)
	# Build the Docker image
	docker build --build-arg APP_VERSION=${VERSION} -t inspq/emr-service:${VERSION} -t inspq/emr-service:latest emr-service
	docker build --build-arg APP_VERSION=${VERSION} -t inspq/emr-web:${VERSION} -t inspq/emr-web:latest emr-web
	
### Deploy the containers from Docker Images

Execute the deploy.yml playbook using this command:

	ansible-playbook -i LOCAL/LOCAL.hosts deploy.yml
	
It is possible to override the defaults Keycloak port, admin user and admin password using the following command:

	ansible-playbook -i LOCAL/LOCAL.hosts -e keycloak_url=http://hostname:newport -e keycloak_user=newuser -e keycloak_password=newPassword deploy.yml
	
The playbook need a Keycloak user/password to create the client and retrieve his client Secret.