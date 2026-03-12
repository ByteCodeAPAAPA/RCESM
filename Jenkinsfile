pipeline {
    agent any

    stages {
        stage('Deploy') {
            steps {
                sh '''
                    # Установка docker-compose если его нет
                    if ! command -v docker-compose &> /dev/null; then
                        echo "Устанавливаем docker-compose..."
                        curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
                        chmod +x /usr/local/bin/docker-compose
                    fi

                    # Проверка наличия файлов
                    echo "Содержимое директории:"
                    ls -la

                    # Запуск контейнеров
                    docker-compose down -v || true
                    docker-compose up -d --build

                    # Проверка статуса
                    docker-compose ps
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "Ожидаем запуск приложения..."
                    timeout=60
                    while ! curl -s http://192.168.0.67:2520/actuator/health; do
                        echo "Ждем... $timeout сек осталось"
                        sleep 5
                        timeout=$((timeout-5))
                        if [ $timeout -le 0 ]; then
                            echo "Таймаут ожидания приложения"
                            exit 1
                        fi
                    done
                    echo "✅ Приложение запущено!"
                '''
            }
        }
    }

    post {
        failure {
            sh '''
                echo "❌ Ошибка! Логи:"
                docker-compose logs || true
                docker ps -a
            '''
        }
        always {
            sh 'docker system prune -f || true'
        }
    }
}