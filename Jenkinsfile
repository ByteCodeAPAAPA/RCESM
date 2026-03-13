pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Start containers') {
            steps {
                script {
                    sh '''
                        echo "Запускаем контейнеры через docker-compose..."

                        # Просто запускаем docker-compose up -d
                        # Он:
                        # - не удаляет существующие контейнеры
                        # - запускает остановленные
                        # - создает только недостающие
                        # - игнорирует ошибки конфликта имен

                        docker-compose up -d || {
                            echo "Ошибка при запуске, пробуем запустить отдельно..."

                            # Запускаем MySQL если он существует
                            if docker ps -a | grep -q rces-mysql; then
                                docker start rces-mysql
                            else
                                docker-compose up -d mysql
                            fi

                            # Запускаем приложение если оно существует
                            if docker ps -a | grep -q rces-app; then
                                docker start rces-app
                            else
                                docker-compose up -d spring-app
                            fi
                        }

                        echo "\\nСтатус контейнеров:"
                        docker ps | grep -E "rces-mysql|rces-app" || true
                    '''
                }
            }
        }

        stage('Wait for services') {
            parallel {
                stage('Wait for MySQL') {
                    steps {
                        script {
                            sh '''
                                echo "Ожидание MySQL..."
                                timeout=60
                                while [ $timeout -gt 0 ]; do
                                    if docker exec rces-mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
                                        echo "MySQL готов!"
                                        break
                                    fi
                                    echo "Ждем MySQL... $timeout сек осталось"
                                    sleep 5
                                    timeout=$((timeout - 5))
                                done

                                if [ $timeout -le 0 ]; then
                                    echo "MySQL не запустился"
                                    docker logs rces-mysql --tail 20
                                    exit 1
                                fi
                            '''
                        }
                    }
                }

                stage('Wait for Spring App') {
                    steps {
                        script {
                            sh '''
                                echo "Ожидание Spring приложения..."
                                timeout=60
                                while [ $timeout -gt 0 ]; do
                                    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://192.168.0.67:2520/actuator/health 2>/dev/null || echo "000")
                                    if [ "$HTTP_CODE" = "200" ]; then
                                        echo "Spring приложение готово!"
                                        break
                                    fi
                                    echo "Ждем приложение... $timeout сек осталось (HTTP: $HTTP_CODE)"
                                    sleep 5
                                    timeout=$((timeout - 5))
                                done

                                if [ $timeout -le 0 ]; then
                                    echo "Приложение не запустилось"
                                    docker logs rces-app --tail 20 2>/dev/null || echo "Логов приложения нет"
                                    exit 1
                                fi
                            '''
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                sh '''
                    echo "\\n=== ИТОГОВЫЙ СТАТУС ==="
                    echo "Запущенные контейнеры:"
                    docker ps --format "table {{.Names}}\\t{{.Status}}\\t{{.Ports}}" | grep -E "rces-mysql|rces-app" || echo "Контейнеры не найдены"
                '''
            }
        }
    }
}