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
                    docker compose down || true
                    docker compose up --build --abort-on-container-exit --exit-code-from tests
                '''
            }
        }
    }

    post {
        always {
            sh '''
                ls -la build
                ls -la build/allure-results || true
                find build/allure-results -maxdepth 2 -type f | head || true
                docker compose down || true
                '''
            script {
                allure([
                    results: [[path: 'build/allure-results']]
                ])
            }
            cleanWs()
        }
    }
}