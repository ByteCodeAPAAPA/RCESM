pipeline {
  agent any
  stages {
    stage('Build') {
      agent {
        docker {
          image 'gradle:8.6-jdk17'
          args '-v /var/run/docker.sock:/var/run/docker.sock'  // доступ к Docker
        }
      }
      steps {
        sh './gradlew clean build'
      }
    }
    stage('Deploy DB and Selenium') {
      steps {
        sh 'docker-compose up -d mysql selenium'
        // можно добавить паузу или healthcheck до готовности сервисов
      }
    }
    stage('DB Migrations') {
      agent {
        docker { image 'boxfuse/flyway:9.16.3' }
      }
      steps {
        sh "/flyway/flyway -url=jdbc:mysql://mysql:3306/rces -user=root -password=adminbms migrate"
      }
    }
  }
}