pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps { git url: 'https://github.com/ByteCodeAPAAPA/RCESM.git', branch: 'home' }
    }
    stage('Start services') {
      steps {
        // Запускаем docker-compose (MySQL, приложение, Selenium)
        sh 'docker-compose up -d'
        // Здесь можно добавить ожидание готовности сервисов
      }
    }
    stage('Build & Test') {
      steps {
        // Сборка и запуск тестов через Gradle
        sh './gradlew clean build -x test'
        sh './gradlew runAllTests'
      }
      post {
        always {
          // Формируем Allure-отчёт
          allure results: [[path: 'build/allure-results']], reportBuildPolicy: 'ALWAYS'
        }
      }
    }
    stage('Stop services') {
      steps {
        sh 'docker-compose down'
      }
    }
  }
}
