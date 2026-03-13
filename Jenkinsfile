pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Check and start containers') {
            steps {
                script {
                    sh '''
                        # Проверяем существование контейнеров
                        MYSQL_EXISTS=$(docker ps -a --filter "name=rces-mysql" --format "{{.Names}}")
                        APP_EXISTS=$(docker ps -a --filter "name=rces-app" --format "{{.Names}}")

                        echo "MySQL контейнер существует: $MYSQL_EXISTS"
                        echo "App контейнер существует: $APP_EXISTS"

                        if [ -n "$MYSQL_EXISTS" ] || [ -n "$APP_EXISTS" ]; then
                            echo "Некоторые контейнеры уже существуют, запускаем их..."

                            # Запускаем MySQL если он существует и остановлен
                            if [ -n "$MYSQL_EXISTS" ]; then
                                MYSQL_RUNNING=$(docker ps --filter "name=rces-mysql" --filter "status=running" --format "{{.Names}}")
                                if [ -z "$MYSQL_RUNNING" ]; then
                                    echo "Запускаем существующий MySQL контейнер..."
                                    docker start rces-mysql
                                else
                                    echo "MySQL уже запущен"
                                fi
                            fi

                            # Запускаем приложение если оно существует и остановлен
                            if [ -n "$APP_EXISTS" ]; then
                                APP_RUNNING=$(docker ps --filter "name=rces-app" --filter "status=running" --format "{{.Names}}")
                                if [ -z "$APP_RUNNING" ]; then
                                    echo "Запускаем существующий контейнер приложения..."
                                    docker start rces-app
                                else
                                    echo "Приложение уже запущено"
                                fi
                            fi

                            # Если приложение не существует, создаем только его через docker-compose
                            if [ -z "$APP_EXISTS" ] && [ -n "$MYSQL_EXISTS" ]; then
                                echo "Приложение не найдено, создаем только контейнер приложения..."
                                # Создаем временный docker-compose файл без MySQL сервиса
                                sed '/mysql:/,/depends_on:/ {
                                    /depends_on:/!d
                                }' docker-compose.yml > docker-compose-app-only.yml
                                docker-compose -f docker-compose-app-only.yml up -d spring-app
                                rm docker-compose-app-only.yml
                            fi
                        else
                            echo "Контейнеры не найдены, создаем все через docker-compose..."
                            docker-compose up -d
                        fi
                    '''
                }
            }
        }

        stage('Verify containers are running') {
            steps {
                script {
                    sh '''
                        # Проверяем что оба контейнера существуют и запущены
                        MYSQL_EXISTS=$(docker ps -a --filter "name=rces-mysql" --format "{{.Names}}")
                        APP_EXISTS=$(docker ps -a --filter "name=rces-app" --format "{{.Names}}")

                        if [ -z "$MYSQL_EXISTS" ]; then
                            echo "ОШИБКА: MySQL контейнер не существует. Создаем..."
                            docker-compose up -d mysql
                        else
                            MYSQL_RUNNING=$(docker ps --filter "name=rces-mysql" --filter "status=running" --format "{{.Names}}")
                            if [ -z "$MYSQL_RUNNING" ]; then
                                echo "Запускаем существующий MySQL контейнер..."
                                docker start rces-mysql
                            fi
                        fi

                        if [ -z "$APP_EXISTS" ]; then
                            echo "ОШИБКА: Контейнер приложения не существует. Создаем..."
                            docker-compose up -d spring-app
                        else
                            APP_RUNNING=$(docker ps --filter "name=rces-app" --filter "status=running" --format "{{.Names}}")
                            if [ -z "$APP_RUNNING" ]; then
                                echo "Запускаем существующий контейнер приложения..."
                                docker start rces-app
                            fi
                        fi

                        echo "Финальный статус контейнеров:"
                        docker ps --filter "name=rces-mysql" --filter "name=rces-app"
                    '''
                }
            }
        }

        stage('Wait for MySQL') {
            steps {
                script {
                    sh '''
                        echo "Ожидаем готовности MySQL..."
                        for i in $(seq 1 30); do
                            if docker exec rces-mysql mysqladmin ping -h localhost --silent; then
                                echo "MySQL готов к работе!"
                                exit 0
                            fi
                            echo "Ждем MySQL... ($i/30)"
                            sleep 5
                        done
                        echo "MySQL не запустился вовремя"
                        docker logs rces-mysql --tail 50
                        exit 1
                    '''
                }
            }
        }

        stage('Wait for Spring App') {
            steps {
                script {
                    sh '''
                        echo "Ожидаем готовности Spring приложения..."
                        for i in $(seq 1 30); do
                            HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://192.168.0.67:2520/actuator/health || true)
                            if [ "$HTTP_CODE" = "200" ]; then
                                echo "Spring приложение готово к работе!"
                                exit 0
                            fi
                            echo "Ждем приложение... ($i/30)"
                            sleep 5
                        done
                        echo "Приложение не запустилось вовремя"
                        docker logs rces-app --tail 50
                        exit 1
                    '''
                }
            }
        }

        stage('Show container status') {
            steps {
                script {
                    sh '''
                        echo "=== СТАТУС КОНТЕЙНЕРОВ ==="
                        docker ps --filter "name=rces-mysql" --filter "name=rces-app"
                        echo ""
                        echo "=== ЛОГИ MYSQL (последние 10 строк) ==="
                        docker logs rces-mysql --tail 10 || echo "MySQL логов нет"
                        echo ""
                        echo "=== ЛОГИ ПРИЛОЖЕНИЯ (последние 10 строк) ==="
                        docker logs rces-app --tail 10 || echo "Логов приложения нет"
                    '''
                }
            }
        }
    }

    post {
        failure {
            script {
                sh '''
                    echo "=== ПОДРОБНЫЕ ЛОГИ ПРИ ОШИБКЕ ==="
                    echo "-- MySQL логи --"
                    docker logs rces-mysql --tail 50 || echo "MySQL не запущен"
                    echo ""
                    echo "-- Логи приложения --"
                    docker logs rces-app --tail 50 || echo "Приложение не запущено"
                    echo ""
                    echo "=== СТАТУС ВСЕХ КОНТЕЙНЕРОВ ==="
                    docker ps -a
                '''
            }
        }
    }
}