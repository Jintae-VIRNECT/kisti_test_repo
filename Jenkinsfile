pipeline {
  agent any
  stages {
    stage('Pre-Build') {
      steps {
        echo 'Pre-Build Stage'
        catchError() {
          sh 'chmod +x ./gradlew'
          sh './gradlew clean'
          sh './gradlew build -x test'
          sh 'cp docker/Dockerfile ./'
        }

      }
    }

    stage('Build') {
      parallel {
        stage('Build') {
          steps {
            echo 'Build Stage'
          }
        }

        stage('Develop Branch') {
          when {
            branch 'develop'
          }
          steps {
            sh 'docker build -t pf-login:develop .'
          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            sh 'docker build -t pf-login:staging .'
          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            sh 'docker build -t pf-login .'
          }
        }

      }
    }

    stage('Test') {
      steps {
        echo 'Test Stage'
      }
    }

    stage('Pre-Deploy') {
      steps {
        echo 'Pre-Deploy Stage'
        catchError() {
          sh 'docker stop pf-login && docker rm pf-login || true'
        }

      }
    }

    stage('Deploy') {
      parallel {
        stage('Deploy') {
          steps {
            echo 'Deploy Stage'
          }
        }

        stage('Develop Branch') {
          when {
            branch 'develop'
          }
          steps {
            sh 'docker run -p 8883:8883 -d --name=pf-login -e "SPRING_PROFILES_ACTIVE=develop" pf-login:develop'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            sh 'docker run -p 8883:8883 -d --name=pf-login -e "SPRING_PROFILES_ACTIVE=staging" pf-login:staging'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            sh 'docker run -p 8883:8883 -d --name=pf-login -e "SPRING_PROFILES_ACTIVE=production" pf-login'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
          }
        }

      }
    }

    stage('Notify') {
      steps {
        emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$platform')
      }
    }

  }
}
