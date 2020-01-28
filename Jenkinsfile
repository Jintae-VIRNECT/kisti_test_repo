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
        sh 'npm install'
        sh 'cp docker/Dockerfile.develop ./'
        sh 'docker build -t pf-webworkstation/develop -f docker/Dockerfile.develop .'
      }
    }

    stage('Test') {
      environment {
        status = 'TestSuccess'
      }
      steps {
        sh 'Test Stage'
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
        emailext(subject: 'PF-WebWorkStation Operated...', body: '${status}', attachLog: true, compressLog: true)
      }
    }

  }
  environment {
    statuss = 'FAIL'
  }
}