pipeline {
    agent any

    options {
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run full stack tests') {
            steps {
                sh '''
                    docker compose down -v || true
                    docker compose up --build --abort-on-container-exit
                '''
            }
        }
    }

    post {
        always {
            sh 'docker compose down -v || true'
            cleanWs()
        }
    }
}