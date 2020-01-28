pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
        status = 'BuildSuccess'
      }
      steps {
        echo 'Build Stage'
        sh 'yarn cache clean'
        sh 'echo \'\' | sudo -S rm yarn.lock'
        sh 'yarn install'
        sh 'yarn workspace smic build'
        sh 'cp docker/Dockerfile.develop ./'
        sh 'docker build -t pf-webworkstation/develop -f docker/Dockerfile.develop .'
      }
    }

    stage('Test') {
      environment {
        status = 'TestSuccess'
      }
      steps {
        echo 'Test Stage'
      }
    }

    stage('Deploy') {
      environment {
        status = 'DeploySuccess'
      }
      steps {
        echo 'Deploy Stage'
        sh '''docker stop pf-webworkstation-develop || true
docker rm pf-webworkstation-develop || true
'''
        sh 'docker run -p 8887:8887 -d --name pf-webworkstation-develop pf-webworkstation/develop'
      }
    }

    stage('Notify') {
      steps {
        emailext(to: 'delbert@virnect.com', subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, from: 'virnect.corp@gmail.com')
      }
    }

  }
  environment {
    status = 'FAIL'
  }
}