pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        COMPOSE_FILE = 'docker-compose.yml'
        COMPOSE_PROJECT_NAME = 'rcesm'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build image') {
            steps {
                sh 'docker compose -f ${COMPOSE_FILE} build app'
            }
        }

        stage('Start stack') {
            steps {
                sh 'docker compose -f ${COMPOSE_FILE} up -d mysql app'
            }
        }

        stage('Wait for MySQL') {
            steps {
                sh '''#!/bin/bash
                set -e
                for i in {1..30}; do
                  if docker compose -f ${COMPOSE_FILE} exec -T mysql mysqladmin ping -uroot -padminbms --silent; then
                    exit 0
                  fi
                  sleep 2
                done
                echo "MySQL did not become ready"
                exit 1
                '''
            }
        }

        stage('Smoke test') {
            steps {
                sh '''#!/bin/bash
                set -e
                docker run --rm --network ${COMPOSE_PROJECT_NAME}_default curlimages/curl:8.10.1 -fsS http://app:2520/actuator/health
                '''
            }
        }
    }

    post {
        always {
            sh 'docker compose -f ${COMPOSE_FILE} down -v || true'
            cleanWs()
        }
    }
}
