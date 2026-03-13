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

                        if [ -n "$MYSQL_EXISTS" ] && [ -n "$APP_EXISTS" ]; then
                            echo "Контейнеры уже существуют, проверяем их состояние..."

                            # Проверяем, запущены ли контейнеры
                            MYSQL_RUNNING=$(docker ps --filter "name=rces-mysql" --filter "status=running" --format "{{.Names}}")
                            APP_RUNNING=$(docker ps --filter "name=rces-app" --filter "status=running" --format "{{.Names}}")

                            # Запускаем MySQL если он остановлен
                            if [ -z "$MYSQL_RUNNING" ]; then
                                echo "Запускаем существующий MySQL контейнер..."
                                docker start rces-mysql
                            else
                                echo "MySQL уже запущен"
                            fi

                            # Запускаем приложение если оно остановлено
                            if [ -z "$APP_RUNNING" ]; then
                                echo "Запускаем существующий контейнер приложения..."
                                docker start rces-app
                            else
                                echo "Приложение уже запущено"
                            fi
                        else
                            echo "Контейнеры не найдены, создаем новые через docker-compose..."
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
                        # Проверяем что оба контейнера запущены
                        MYSQL_RUNNING=$(docker ps --filter "name=rces-mysql" --filter "status=running" --format "{{.Names}}")
                        APP_RUNNING=$(docker ps --filter "name=rces-app" --filter "status=running" --format "{{.Names}}")

                        if [ -z "$MYSQL_RUNNING" ]; then
                            echo "ОШИБКА: MySQL контейнер не запущен"
                            exit 1
                        fi

                        if [ -z "$APP_RUNNING" ]; then
                            echo "ОШИБКА: Контейнер приложения не запущен"
                            exit 1
                        fi

                        echo "Все контейнеры успешно запущены:"
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
                        docker logs rces-mysql --tail 10
                        echo ""
                        echo "=== ЛОГИ ПРИЛОЖЕНИЯ (последние 10 строк) ==="
                        docker logs rces-app --tail 10
                    '''
                }
            }
        }
    }

    post {
        failure {
            script {
                // При ошибке показываем логи для диагностики
                sh '''
                    echo "=== ПОДРОБНЫЕ ЛОГИ ПРИ ОШИБКЕ ==="
                    echo "-- MySQL логи --"
                    docker logs rces-mysql --tail 50 || true
                    echo ""
                    echo "-- Логи приложения --"
                    docker logs rces-app --tail 50 || true
                '''
            }
        }


        always {
            script {
                sh 'docker stop rces-mysql rces-app || true'
            }
        }
    }
}