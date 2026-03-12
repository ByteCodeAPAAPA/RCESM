pipeline {
    agent any
    stages {
        stage('Deploy') {
            steps {
                sh '''
                    docker-compose down -v || true
                    docker-compose up -d --build
                    sleep 10
                    curl -f http://192.168.0.67:2520/actuator/health
                '''
            }
        }
    }
    post {
        failure {
            sh 'docker-compose logs'
        }
        always {
            sh 'docker system prune -f || true'
        }
    }
}