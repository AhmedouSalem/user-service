pipeline {
	agent any

    tools {
		maven 'Maven_3'
    }

    environment {
		DOCKER_IMAGE_PREFIX = "ecom"
        SERVICE_NAME = "user-service"
    }

    stages {
		stage('Checkout') {
			steps {
				echo "🔄 Clonage du code source..."
                checkout scm
                script {
					// Afficher infos Git
                    def branch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    def commit = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.GIT_BRANCH = branch
                    env.GIT_COMMIT = commit
                    env.VERSION = "v1.0-${commit}"
                    echo "📚 Branche Git : ${branch}"
                    echo "🔖 Commit Git : ${commit}"
                    echo "🏷️ Version du build : ${VERSION}"
                }
            }
        }

        stage('Build & Test') {
			steps {
				echo "🏗️ Build Maven + Tests unitaires..."
                sh 'mvn clean install -DskipTests=false'
            }
        }

        stage('SonarQube Analysis') {
			steps {
				echo "🔎 Analyse SonarQube..."
                withSonarQubeEnv('SonarQube') {
					sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
			steps {
				echo "🐳 Construction de l'image Docker ${DOCKER_IMAGE_PREFIX}-${SERVICE_NAME}:${VERSION}..."
                sh '''
                    docker build -t $DOCKER_IMAGE_PREFIX-$SERVICE_NAME:$VERSION .
                '''
            }
        }

        stage('Tests d’intégration (placeholder)') {
			steps {
				echo "🔗 Tests d'intégration ici (non implémentés encore)."
            }
        }

        stage('Notification (placeholder)') {
			steps {
				echo "📢 Notification Slack/Email (à configurer plus tard)."
            }
        }
    }

    post {
		always {
			echo '🎯 Pipeline terminé (always block).'
        }
        success {
			echo '✅ Succès du pipeline.'
        }
        failure {
			echo '💥 Échec du pipeline.'
        }
    }
}
