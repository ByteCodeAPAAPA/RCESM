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

        stage('Wait and test') {
            steps {
                sh 'sleep 30'
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