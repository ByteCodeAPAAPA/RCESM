pipeline {
    agent any
    stages {
        stage('Deploy') {
            steps {
                sh '''
                    # Даем права на выполнение gradlew
                    chmod +x gradlew

                    # Сборка через gradlew в Docker контейнере
                    docker run --rm \
                        -v $(pwd):/app \
                        -w /app \
                        gradle:7.6-jdk17 \
                        ./gradlew clean bootWar -x bootRun

                    docker-compose down -v || true
                    docker-compose up -d --build
                    sleep 20
                '''
            }
        }
    }
}