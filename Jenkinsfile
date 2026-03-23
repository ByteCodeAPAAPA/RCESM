pipeline {
    agent any

    environment {
        GRADLE_USER_HOME = '/tmp/.gradle-cache'
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
                    sh "docker exec rces-app mkdir -p /tmp/test-${BUILD_NUMBER}"
                    sh "docker cp ${WORKSPACE}/. rces-app:/tmp/test-${BUILD_NUMBER}/"
                    sh "docker exec rces-app chmod +x /tmp/test-${BUILD_NUMBER}/gradlew"

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
                        // Используем docker cp для копирования всей директории
                        sh """
                            # Создаем временную директорию
                            mkdir -p /tmp/allure-results-${BUILD_NUMBER}

                            # Копируем из контейнера rces-app во временную директорию
                            docker cp rces-app:/tmp/test-${BUILD_NUMBER}/build/allure-results/. /tmp/allure-results-${BUILD_NUMBER}/ 2>/dev/null || echo "No allure results"

                            # Копируем из временной директории в workspace
                            cp -r /tmp/allure-results-${BUILD_NUMBER}/* ${WORKSPACE}/build/allure-results/ 2>/dev/null || echo "Copy failed"

                            # Очищаем временную директорию
                            rm -rf /tmp/allure-results-${BUILD_NUMBER}

                            # Копируем отчеты
                            docker cp rces-app:/tmp/test-${BUILD_NUMBER}/build/reports ${WORKSPACE}/build/reports 2>/dev/null || echo "No test reports"
                        """

                        archiveArtifacts artifacts: 'build/**/*', allowEmptyArchive: true
                    }
                }
            }
        }

        stage('Cleanup') {
            steps {
                script {
                    sh 'docker-compose down'
                    sh 'docker exec rces-app rm -rf /tmp/test-${BUILD_NUMBER} || true'
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                script {
                    // Проверяем наличие результатов
                    sh 'ls -la build/allure-results/ || echo "No results"'

                    def fileCount = sh(
                        script: 'find build/allure-results -name "*.json" 2>/dev/null | wc -l',
                        returnStdout: true
                    ).trim()

                    echo "Found ${fileCount} Allure result files"

                    if (fileCount.toInteger() > 0) {
                        allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'build/allure-results']]
                        ])
                        echo "✅ Allure report generated"
                    } else {
                        echo "⚠️ No Allure results found"
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                cleanWs()
            }
        }
        success {
            script {
                currentBuild.description = "✅ Tests passed! Allure: ${env.BUILD_URL}allure"
            }
            echo '✅ Все тесты успешно пройдены!'
        }
        failure {
            script {
                currentBuild.description = "❌ Tests failed. Check Allure: ${env.BUILD_URL}allure"
            }
            echo '❌ Тесты завершились с ошибками'
            junit testResults: 'build/reports/tests/**/*.xml', allowEmptyResults: true
        }
    }
}