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
                       docker run -it --rm -v /var/lib/docker/volumes/jenkins_home/_data/workspace/pipeline:/app -w /app gradle:7.6-jdk17 bash
                       docker-compose up -d
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