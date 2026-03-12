pipeline {
    agent any

    environment {
        COMPOSE_FILE = 'docker-compose.yml'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/your-repo/your-app.git'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker-compose -f ${COMPOSE_FILE} down -v || true
                    docker-compose -f ${COMPOSE_FILE} up -d --build
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    timeout 60 sh -c 'until curl -s http://192.168.0.67:2520/actuator/health; do sleep 2; done'
                '''
            }
        }
    }

    post {
        failure {
            sh 'docker-compose -f ${COMPOSE_FILE} logs --tail=50'
            sh 'docker-compose -f ${COMPOSE_FILE} down -v'
        }
        always {
            sh 'docker system prune -f || true'
        }
    }
}