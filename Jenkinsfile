pipeline {
    agent any
    stages {
        stage('Deploy') {
            steps {
                sh '''
                    # Сборка приложения в Docker контейнере используя gradle напрямую
                    docker run --rm \
                        -v "$(pwd):/app" \
                        -w /app \
                        gradle:7.6-jdk17 \
                        gradle clean bootWar -x test

                    # Проверка результатов сборки
                    ls -la build/libs/ || echo "No build/libs directory"

                    # Запуск контейнеров
                    docker-compose down -v || true
                    docker-compose up -d --build

                    # Ожидание и проверка
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