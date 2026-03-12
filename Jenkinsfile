pipeline {
    agent any
    stages {
        stage('Check files') {
            steps {
                sh '''
                    echo "=== СОДЕРЖИМОЕ КОРНЕВОЙ ДИРЕКТОРИИ ==="
                    ls -la

                    echo "=== ПОИСК GRADLE ФАЙЛОВ ==="
                    find . -name "build.gradle*" -o -name "settings.gradle*" -o -name "*.kts" | head -20

                    echo "=== ТЕКУЩАЯ ДИРЕКТОРИЯ ==="
                    pwd
                '''
            }
        }
        stage('Build') {
            steps {
                sh '''
                    # Если проект в поддиректории
                    if [ -d "backend" ]; then
                        cd backend
                    elif [ -d "app" ]; then
                        cd app
                    elif [ -d "src" ]; then
                        cd src
                    fi

                    # Пытаемся собрать
                    docker run --rm \
                        -v "$(pwd):/app" \
                        -w /app \
                        gradle:7.6-jdk17 \
                        gradle tasks || true
                '''
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                    docker-compose down -v || true
                    docker-compose up -d --build
                '''
            }
        }
    }
}