pipeline {
    agent any
    stages {
        stage('Deploy') {
            steps {
                sh '''
                    # Сборка приложения
                    gradle clean bootWar -x test || ./gradlew clean bootWar -x test

                    # Запуск контейнеров
                    docker-compose down -v
                    docker-compose up -d --build

                    # Проверка
                    sleep 20
                    curl -f http://192.168.0.67:2520/actuator/health || true
                '''
            }
        }
    }
    post {
        always {
            sh 'docker system prune -f || true'
        }
    }
}