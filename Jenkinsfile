pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run stack and tests') {
            steps {
                sh '''
                    docker compose down -v || true
                    docker compose up --build --abort-on-container-exit --exit-code-from tests
                '''
            }
        }
    }

    post {
        always {
            sh 'docker compose down -v || true'

            script {
                allure([
                    results: [[path: 'build/allure-results']]
                ])
            }

            cleanWs()
        }
    }
}