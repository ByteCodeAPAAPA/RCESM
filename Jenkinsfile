pipeline {
    agent any

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
                    // Копируем во временную директорию
                    sh 'docker exec rces-app mkdir -p /tmp/test'
                    sh "docker cp ${WORKSPACE}/. rces-app:/tmp/test/"

                    // Запускаем тесты из временной директории
                    sh 'docker exec rces-app chmod +x /tmp/test/gradlew'
                    sh 'docker exec -w /tmp/test rces-app ./gradlew runAllTests'
                }
            }
        }

        stage('Cleanup') {
            steps {
                sh 'docker-compose down'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}