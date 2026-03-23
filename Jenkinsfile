pipeline {
    agent any

    environment {
        IMAGE_NAME = 'rces-app'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'home', url: 'https://github.com/ByteCodeAPAAPA/RCESM.git'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $IMAGE_NAME .'
            }
        }

        stage('Run App') {
            steps {
                sh 'docker-compose up -d'
            }
        }

        stage('Healthcheck') {
            steps {
                sh '''
                for i in {1..10}; do
                  curl -f http://localhost:2520/login && break
                  sleep 5
                done
                '''
            }
        }

        stage('Run Tests') {
            steps {
                sh './gradlew runAllTests'
            }
        }

        stage('Allure Report') {
            steps {
                allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            }
        }
    }

    post {
        always {
            sh 'docker-compose down'
        }
    }
}