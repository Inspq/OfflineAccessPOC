#!/usr/bin/env groovy
pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }
    tools {
        jdk 'JDK1.8.0_161'
        maven 'M3'
    }
    stages {
        stage ("emr-service build") {
            steps {
                sh "mvn clean install -Pproduction"
            }
            post {
                success {
                    archive '**/target/*.jar'
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }
        stage ("Déployer les artifacts sur le serveur Nexus") {
            steps {
                sh "mvn deploy -Dmaven.install.skip=true -Dmaven.test.skip=true"
            }
        }
        stage ("Package offlineAccess Docker image") {
            environment {
			    offlineAccess = readMavenPom file: 'pom.xml'
		    	VERSION = offlineAccess.getVersion()
		    	emrService = readMavenPom file: 'emr-service/pom.xml'
		    	ARTIFACT = emrService.getArtifactId().toLowerCase()
			}
            steps {
                sh "docker build --build-arg APP_VERSION=${VERSION} -t nexus3.inspq.qc.ca:5000/inspq/${ARTIFACT}:${VERSION} -t nexus3.inspq.qc.ca:5000/inspq/${ARTIFACT}:latest ."
                sh "docker push nexus3.inspq.qc.ca:5000/inspq/${ARTIFACT}:${VERSION}"
                sh "docker push nexus3.inspq.qc.ca:5000/inspq/${ARTIFACT}:latest"
            }
        }
    }
    post {
        always {
            script {
                equipe = 'mathieu.couture@inspq.qc.ca,etienne.sadio@inspq.qc.ca,soleman.merchan@inspq.qc.ca,philippe.gauthier@inspq.qc.ca,pierre-olivier.chiasson@inspq.qc.ca,Patrick.Roberge@inspq.qc.ca,Eric.Parent@inspq.qc.ca'
            }
        }
        success {
            script {
                if (currentBuild.getPreviousBuild() == null || (currentBuild.getPreviousBuild() != null && currentBuild.getPreviousBuild().getResult().toString() != "SUCCESS")) {
                    mail(to: "${equipe}", 
                        subject: "Construction de sx5-habilitation réalisée avec succès: ${env.JOB_NAME} #${env.BUILD_NUMBER}", 
                        body: "${env.BUILD_URL}")
                }
            }
        }
        failure {
            mail(to: "${equipe}",
                subject: "Échec de la construction de sx5-habilitation : ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "${env.BUILD_URL}")
        }
        unstable {
            mail(to : "${equipe}",
                subject: "Construction de sx5-habilitation instable : ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "${env.BUILD_URL}")
        }
    }
}