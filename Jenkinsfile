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
                        echo "Запускаем контейнеры..."

                        # Запускаем MySQL (существующий или новый)
                        if docker ps -a | grep -q rces-mysql; then
                            echo "MySQL контейнер существует, запускаем..."
                            docker start rces-mysql
                        else
                            echo "Создаем новый MySQL контейнер..."
                            docker-compose up -d mysql
                        fi

                        # Запускаем приложение (существующее или новое)
                        if docker ps -a | grep -q rces-app; then
                            echo "App контейнер существует, запускаем..."
                            docker start rces-app
                        else
                            echo "Создаем новый контейнер приложения..."
                            # Важно: используем --no-deps чтобы не создавать MySQL заново
                            docker-compose up -d --no-deps spring-app
                        fi

                        echo "\\nСтатус контейнеров:"
                        docker ps | grep -E "rces-mysql|rces-app" || echo "Контейнеры не найдены"
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
                                        echo "✅ MySQL готов!"
                                        break
                                    fi
                                    echo "Ждем MySQL... $timeout сек осталось"
                                    sleep 5
                                    timeout=$((timeout - 5))
                                done

                                if [ $timeout -le 0 ]; then
                                    echo "❌ MySQL не запустился"
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
                                    # Проверяем, запущен ли контейнер
                                    if ! docker ps | grep -q rces-app; then
                                        echo "Контейнер приложения не запущен, пробуем запустить..."
                                        docker start rces-app 2>/dev/null || docker-compose up -d --no-deps spring-app
                                    fi

                                    # Проверяем health endpoint
                                    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://192.168.0.67:2520/actuator/health 2>/dev/null || echo "000")
                                    if [ "$HTTP_CODE" = "200" ]; then
                                        echo "✅ Spring приложение готово!"
                                        break
                                    fi
                                    echo "Ждем приложение... $timeout сек осталось (HTTP: $HTTP_CODE)"
                                    sleep 5
                                    timeout=$((timeout - 5))
                                done

                                if [ $timeout -le 0 ]; then
                                    echo "❌ Приложение не запустилось"
                                    docker logs rces-app --tail 20 2>/dev/null || echo "Логов приложения нет"
                                    exit 1
                                fi
                            '''
                        }
                    }
                }
            }
        }

        stage('Show application info') {
            steps {
                script {
                    sh '''
                        echo "\\n=== ИНФОРМАЦИЯ О ПРИЛОЖЕНИИ ==="
                        echo "MySQL: порт 3307 на хосте"
                        echo "Приложение: http://192.168.0.67:2520"
                        echo ""
                        echo "Логи приложения (последние 10 строк):"
                        docker logs rces-app --tail 10 2>/dev/null || echo "Логов нет"
                    '''
                }
            }
        }
    }

    post {
        failure {
            script {
                sh '''
                    echo "\\n=== ДИАГНОСТИКА ОШИБКИ ==="
                    echo "1. Все контейнеры:"
                    docker ps -a

                    echo ""
                    echo "2. Логи MySQL:"
                    docker logs rces-mysql --tail 20 2>/dev/null || echo "MySQL логов нет"

                    echo ""
                    echo "3. Логи приложения:"
                    docker logs rces-app --tail 20 2>/dev/null || echo "Логов приложения нет"

                    echo ""
                    echo "4. Проверка портов:"
                    netstat -tulpn | grep 2520 || echo "Порт 2520 не прослушивается"
                    netstat -tulpn | grep 3307 || echo "Порт 3307 не прослушивается"
                '''
            }
        }

        success {
            script {
                sh '''
                    echo "\\n✅ Все сервисы успешно запущены!"
                    echo "MySQL: порт 3307"
                    echo "Приложение: http://192.168.0.67:2520"
                '''
            }
        }
    }
}