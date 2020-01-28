pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Build Stage'
        catchError() {
          sh 'chmod +x ./gradlew'
          sh './gradlew clean'
          sh './gradlew cleanQuerydslSourcesDir'
          sh './gradlew build -x test'
          sh 'docker build -t pf-workspace/develop -f docker/Dockerfile.develop .'
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
          sh 'docker stop pf-workspace-develop && docker rm pf-workspace-develop || true'
          sh 'docker run -p 8082:8082 -d --name=pf-workspace-develop pf-workspace/develop'
        }

      }
    }

    stage('Notify') {
      steps {
        emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$DEFAULT_RECIPIENTS')
      }
    }

  }
}