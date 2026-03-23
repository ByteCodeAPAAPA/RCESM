pipeline {
    agent any

    environment {
        GRADLE_USER_HOME = '/tmp/.gradle-cache'
        ALLURE_RESULTS = 'build/allure-results'
        ALLURE_REPORT = 'build/allure-report'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare environment') {
            steps {
                script {
                    def volumePath = sh(
                        script: "docker volume inspect jenkins_home --format '{{ .Mountpoint }}'",
                        returnStdout: true
                    ).trim()

                    env.HOST_WORKSPACE = "${volumePath}/workspace/${env.JOB_BASE_NAME}"
                    echo "HOST_WORKSPACE = ${env.HOST_WORKSPACE}"

                    sh 'mkdir -p build/allure-results'
                    sh 'mkdir -p build/reports'
                }
            }
        }

        stage('Prepare permissions') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Run docker-compose') {
            steps {
                script {
                    sh "export HOST_WORKSPACE='${env.HOST_WORKSPACE}' && docker-compose up -d"
                }
            }
        }

        stage('Wait for app') {
            steps {
                timeout(time: 120, unit: 'SECONDS') {
                    waitUntil(initialRecurrencePeriod: 5000) {
                        script {
                            sh(script: """
                                curl -s --fail http://host.docker.internal:2520/login \
                                | grep -q 'login'
                            """, returnStatus: true) == 0
                        }
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    // Создаем директорию для Allure результатов
                    sh "docker exec rces-app mkdir -p /tmp/test-${BUILD_NUMBER}/build/allure-results"

                    // Копируем тесты
                    sh "docker cp ${WORKSPACE}/. rces-app:/tmp/test-${BUILD_NUMBER}/"
                    sh "docker exec rces-app chmod +x /tmp/test-${BUILD_NUMBER}/gradlew"

                    // Запускаем тесты с Allure
                    sh """
                        docker exec \\
                            -e BASE_URL=http://host.docker.internal:2520 \\
                            -e HEADLESS=true \\
                            -e SELENIUM_REMOTE_URL=http://host.docker.internal:4444/wd/hub \\
                            -w /tmp/test-${BUILD_NUMBER} \\
                            rces-app ./gradlew runAllTests \\
                            -DBASE_URL=http://host.docker.internal:2520 \\
                            -DHEADLESS=true \\
                            -DSELENIUM_REMOTE_URL=http://host.docker.internal:4444/wd/hub \\
                            -Dallure.results.dir=/tmp/test-${BUILD_NUMBER}/build/allure-results
                    """
                }
            }
            post {
                always {
                    script {
                        // Копируем Allure результаты из контейнера даже при падении тестов
                        sh """
                            docker cp rces-app:/tmp/test-${BUILD_NUMBER}/build/allure-results ${WORKSPACE}/build/allure-results || echo "No allure results found"
                        """

                        // Сохраняем Allure результаты как артефакты
                        archiveArtifacts artifacts: 'build/allure-results/**/*', allowEmptyArchive: true
                    }
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                script {
                    // Генерируем Allure отчет
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'build/allure-results']]
                    ])
                }
            }
            post {
                always {
                    script {
                        // Публикуем Allure отчет даже при падении тестов
                        publishHTML([
                            allowMissing: true,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'build/allure-report',
                            reportFiles: 'index.html',
                            reportName: 'Allure Report'
                        ])
                    }
                }
            }
        }

        stage('Cleanup') {
            steps {
                sh 'docker-compose down'
                sh 'docker exec rces-app rm -rf /tmp/test-${BUILD_NUMBER} || true'
            }
        }
    }

    post {
        always {
            script {
                // Всегда копируем и публикуем Allure результаты
                try {
                    // Копируем Allure результаты из контейнера если еще не скопировали
                    sh """
                        docker cp rces-app:/tmp/test-${BUILD_NUMBER}/build/allure-results ${WORKSPACE}/build/allure-results || echo "No results to copy"
                    """

                    // Генерируем финальный отчет
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'build/allure-results']]
                    ])

                    // Публикуем HTML отчет
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/allure-report',
                        reportFiles: 'index.html',
                        reportName: 'Allure Report'
                    ])
                } catch (Exception e) {
                    echo "Allure report generation failed: ${e.message}"
                }
            }

            cleanWs()
        }
        success {
            script {
                currentBuild.description = "✅ Allure report: ${env.BUILD_URL}allure"
            }
            echo '✅ Все тесты успешно пройдены!'
        }
        failure {
            script {
                currentBuild.description = "❌ Failed. Allure report: ${env.BUILD_URL}allure"
            }
            echo '❌ Тесты завершились с ошибками. Проверьте логи и Allure отчет.'
        }
        unstable {
            script {
                currentBuild.description = "⚠️ Unstable. Allure report: ${env.BUILD_URL}allure"
            }
            echo '⚠️ Тесты прошли с ошибками (unstable). Проверьте Allure отчет.'
        }
    }
}