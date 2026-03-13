pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Start containers') {
            steps {
                script {
                    sh '''
                       docker-compose up
                    '''
                }
            }
        }
    }

    post {
        success {
            script {
                sh '''
                    echo "\\n✅ Все сервисы успешно запущены!"
                    echo "MySQL: порт 3307"
                    echo "Приложение: http://192.168.0.67:2520"
                '''
            }
        }
    }
}