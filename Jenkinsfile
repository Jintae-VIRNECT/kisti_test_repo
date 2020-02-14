pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Build Stage'
        catchError() {
          sh 'yarn cache clean'
          sh '"echo <password> | sudo -S rm yarn.lock" || true'
          sh 'yarn install'
          sh 'yarn workspace smic build'
          sh 'cp docker/Dockerfile.develop ./'
          sh 'docker build -t pf-webworkstation/develop -f docker/Dockerfile.develop .'
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
        echo 'Deploy Stage'
        catchError() {
          sh '''docker stop pf-webworkstation-develop || true
docker rm pf-webworkstation-develop || true
'''
          sh 'docker run -p 8887:8887 -d --name pf-webworkstation-develop pf-webworkstation/develop'
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