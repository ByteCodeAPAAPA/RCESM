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
                ls -la build
                ls -la build/allure-results || true
                    docker compose down || true
                    ls -la build
                    ls -la build/allure-results || true
                    docker compose up --build --abort-on-container-exit --exit-code-from tests
                    ls -la build
                    ls -la build/allure-results || true
                '''
            }
        }
    }

    post {
        always {
            sh 'ls -la build
                ls -la build/allure-results || true
                docker compose down || true'
            script {
                allure([
                    results: [[path: 'build/allure-results']]
                ])
            }
            cleanWs()
        }
    }
}