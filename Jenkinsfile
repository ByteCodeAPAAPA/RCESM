pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Забирает код из репозитория (работает, если Jenkinsfile в SCM)
                checkout scm
            }
        }

        stage('Start Docker Compose') {
            steps {
                script {
                    // Поднимаем контейнеры в фоне
                    sh 'docker-compose up -d'
                }
            }
        }

        stage('Verify containers') {
            steps {
                script {
                    // Проверяем, что оба контейнера присутствуют в списке запущенных
                    sh 'docker ps | grep rces-mysql'
                    sh 'docker ps | grep rces-app'
                }
            }
        }

        stage('Wait for Spring App') {
            steps {
                script {
                    // Ждём, пока приложение начнёт отвечать по HTTP (health check)
                    sh '''
                        for i in $(seq 1 30); do
                            HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:2520/actuator/health || true)
                            if [ "$HTTP_CODE" = "200" ]; then
                                echo "Application is ready!"
                                exit 0
                            fi
                            echo "Waiting for application... ($i/30)"
                            sleep 5
                        done
                        echo "Application failed to start"
                        exit 1
                    '''
                }
            }
        }
    }

    post {
        always {
            script {
                // Останавливаем контейнеры и удаляем сеть (volume'ы сохраняются)
                sh 'docker-compose down'
                // Если нужно удалить и volume'ы, добавьте флаг -v: docker-compose down -v
            }
        }
    }
}