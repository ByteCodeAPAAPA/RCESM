pipeline {
    agent any
    stages {
        stage('Clean') {
            steps {
                sh '''
                    echo "=== Полная очистка ==="
                    # Удалить всё: контейнеры, сети, volumes
                    docker-compose down -v --remove-orphans || true

                    # Удалить конкретные контейнеры если остались
                    docker rm -f $(docker ps -aq) 2>/dev/null || true

                    # Очистить все неиспользуемые ресурсы
                    docker system prune -af --volumes
                '''
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                    echo "=== Свежий запуск ==="
                    docker-compose up -d --build --force-recreate

                    echo "=== Ожидание запуска ==="
                    sleep 20

                    echo "=== Статус контейнеров ==="
                    docker ps

                    echo "=== Проверка приложения ==="
                    curl -f http://192.168.0.67:2520/actuator/health || echo "Health check не доступен"
                '''
            }
        }
    }
}