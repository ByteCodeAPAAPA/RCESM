pipeline {
    agent any

    environment {
        HOST_WORKSPACE = sh(
            script: """
                volume_path=\$(docker volume inspect jenkins_home --format '{{ .Mountpoint }}')
                echo "\${volume_path}/workspace/${env.JOB_BASE_NAME}"
            """,
            returnStdout: true
        ).trim()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
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
                    sh "HOST_WORKSPACE='${HOST_WORKSPACE}' docker-compose up -d"
                }
            }
        }

        stage('Wait for app') {
            steps {
                waitUntil(timeout: 50, initialRecurrencePeriod: 15000) {
                    script {
                        sh(script: "curl -s --fail http://192.168.0.67:2520", returnStatus: true) == 0
                    }
                }
            }
        }

        stage('Test') {
            steps {
                sh './gradlew runAllTests'
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