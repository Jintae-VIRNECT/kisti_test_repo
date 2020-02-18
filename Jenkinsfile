pipeline {
  agent any
  stages {
    stage('Pre-Build') {
      steps {
        echo 'Build Stage'
        catchError() {
          sh 'chmod +x ./gradlew'
          sh './gradlew clean'
          sh './gradlew cleanQuerydslSourcesDir'
          sh './gradlew build -x test'          
        }

      }
    }
    stage('Build') {
      steps {
        echo 'Build Stage'
        catchError() {
          sh 'docker build -t pf-message/develop -f docker/Dockerfile.develop .'
        }

      }
    }

    stage('Test') {
      steps {
        echo 'Test Stage'
      }
    }

    stage('Deploy') {
      steps {
        catchError() {
          sh 'docker stop pf-message-develop && docker rm pf-message-develop || true'
          sh 'docker run -p 8084:8084 -d --name=pf-message-develop pf-message/develop'
          sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
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