pipeline {
	agent any

    tools {
		maven 'Maven_3'
    }

    environment {
		DOCKER_IMAGE_PREFIX = "ecom"
        VERSION = "v1.0.${BUILD_NUMBER}"
    }

    stages {
		stage('Prepare') {
			steps {
				echo "🔄 Préparation du build..."
            }
        }

        stage('Build & Test') {
			steps {
				sh 'mvn clean install -DskipTests=false'
            }
        }

        stage('SonarQube Analysis') {
			steps {
				withSonarQubeEnv('SonarQube') {
					sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
			steps {
				echo "🐳 Construction de l'image Docker..."
                sh '''
                    docker build -t $DOCKER_IMAGE_PREFIX-user-service:$VERSION .
                '''
            }
        }
    }

    post {
		always {
			echo '🎯 Pipeline terminé !'
        }
        failure {
			echo '💥 Échec du pipeline.'
        }
    }
}
