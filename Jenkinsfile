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
                    sh 'docker exec rces-app mkdir -p /tmp/test-${BUILD_NUMBER}'
                    sh "docker cp ${WORKSPACE}/. rces-app:/tmp/test-${BUILD_NUMBER}/"
                    sh "docker exec rces-app chmod +x /tmp/test-${BUILD_NUMBER}/gradlew"

                    sh """
                        docker exec \\
                            -e BASE_URL=http://host.docker.internal:2520 \\
                            -e HEADLESS=true \\
                            -e SELENIUM_REMOTE_URL=http://host.docker.internal:4444/wd/hub \\
                            -w /tmp/test-${BUILD_NUMBER} \\
                            rces-app ./gradlew runAllTests
                    """
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
            cleanWs()
        }
        success {
            echo '✅ Все тесты успешно пройдены!'
        }
        failure {
            echo '❌ Тесты завершились с ошибками. Проверьте логи.'
        }
    }
}