pipeline {
	agent any

    tools {
		maven 'Maven_3'
    }

    environment {
		DOCKER_IMAGE_PREFIX = "ecom"
        SERVICE_NAME = "user-service" // À adapter pour chaque service
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

        stage('Check Changes and Abort if None') {
			steps {
				script {
					echo "🔎 Vérification des changements dans src/ ou pom.xml..."
                    def hasChanges = sh(script: "git diff --name-only HEAD~1 HEAD | grep -E '(src/|pom.xml)' || true", returnStatus: true) == 0
                    if (!hasChanges) {
						echo "⏹️ Aucun changement détecté dans src/ ou pom.xml. Arrêt du pipeline proprement."
                        currentBuild.result = 'SUCCESS'
                        error('⏹️ Build arrêté car aucun changement détecté.')
                    }
                    echo "✅ Changements détectés, on continue."
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

        //stage('Docker Push (optionnel)') {
		//	when {
		//		expression {
		//			return env.DOCKER_PUSH == 'true'
        //        }
        //    }
        //    steps {
		//		echo "🚀 Pousser l'image Docker dans le registre..."
        //        sh '''
        //            docker tag $DOCKER_IMAGE_PREFIX-$SERVICE_NAME:$VERSION your-dockerhub-username/$SERVICE_NAME:$VERSION
        //            docker push your-dockerhub-username/$SERVICE_NAME:$VERSION
        //        '''
        //    }
        //}

        stage('Tests d’intégration (placeholder)') {
			steps {
				echo "🔗 Tests d'intégration (non implémentés encore)."
            }
        }

        stage('Notification (placeholder)') {
			steps {
				echo "📢 Notification Email (à configurer plus tard)."
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
