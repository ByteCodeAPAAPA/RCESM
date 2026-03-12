pipeline {
    agent any
    stages {
        stage('Deploy') {
            steps {
                sh '''
                    # Очистка и запуск
                    docker-compose down -v || true
                    docker-compose up -d --build

                    # Проверка
                    sleep 30
                    curl -f http://192.168.0.67:2520/actuator/health || echo "Health check endpoint not ready"
                '''
            }
        }
    }
    post {
        always {
            sh 'docker system prune -f || true'
        }
        failure {
            sh 'docker-compose logs'
        }
    }
}