pipeline {
    agent any
    stages {
        stage('Deploy') {
            steps {
                sh '''
                    chmod +x gradlew

                    # Берем путь в кавычки из-за пробела
                    docker run --rm \
                        -v "$(pwd):/app" \
                        -w /app \
                        gradle:7.6-jdk17 \
                        ./gradlew clean bootWar -x test

                    docker-compose down -v || true
                    docker-compose up -d --build
                    sleep 30
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