pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Build Stage'
        catchError(catchInterruptions: true, buildResult: 'FAILURE') {
          sh 'npm cache verify'
          sh 'npm install'
          sh 'npm run build:develop'
          sh 'cp docker/Dockerfile.develop ./'
          sh 'docker build -t rm-web/develop -f docker/Dockerfile.develop .'
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
          sh '''docker stop rm-web-develop || true
docker rm rm-web-develop || true'''
          sh 'docker run -p 8886:8886 -d --name rm-web-develop rm-web/develop'
          sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
        }

      }
    }

    stage('Notify') {
      steps {
        echo 'Notify Stage'
        emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: 'delbert@virnect.com wooka@virnect.com')
      }
    }

  }
}