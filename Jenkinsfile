pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    stages {

        stage('Clean') {
            steps {
                deleteDir()
            }
        }

        stage('Checkout') {
            steps {
                git 'https://github.com/ByteCodeAPAAPA/RCESM.git'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Run MySQL + Selenium') {
            steps {
                sh 'docker-compose up -d mysql selenium'
                sh 'sleep 20'
            }
        }

        stage('Flyway миграции') {
            steps {
                sh '''
                docker run --rm \
                --network host \
                boxfuse/flyway:9.16.3 \
                -url=jdbc:mysql://localhost:3306/rces \
                -user=root \
                -password=adminbms \
                migrate
                '''
            }
        }

        stage('Run app') {
            steps {
                sh 'docker build -t rces-app .'
                sh 'docker run -d -p 2520:2520 --name rces --network host rces-app'
            }
        }
    }

    post {
        always {
            sh 'docker-compose down'
            sh 'docker rm -f rces || true'
        }
    }
}