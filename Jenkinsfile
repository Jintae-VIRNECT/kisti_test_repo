pipeline {
  agent any
  stages {
    stage('Clean Old Artifacts') {
      steps {
        echo 'Clean Old Artifacts Stage'
        sh '''yarn cache clean
'''
      }
    }

    stage('Build') {
      steps {
        echo 'Build Stage'
        sh 'npm install'
        catchError(buildResult: 'FAILURE')
      }
    }

    stage('Build Dockerfile') {
      steps {
        echo 'Dockerizing Stage'
        sh 'cp docker/Dockerfile.develop ./'
        sh 'docker build -t pf-webworkstation/develop -f docker/Dockerfile.develop .'
        catchError(buildResult: 'FAILURE')
      }
    }

    stage('Clean Old Container') {
      steps {
        echo 'Clean Old Container Stage'
        sh '''docker stop pf-webworkstation-develop || true
docker rm pf-webworkstation-develop || true'''
        catchError(buildResult: 'FAILURE')
      }
    }

    stage('Deploy') {
      steps {
        echo 'Deploy Stage'
        sh 'docker run -p 8887:8887 -d --name pf-webworkstation-develop pf-webworkstation/develop'
        catchError(buildResult: 'SUCCESS')
      }
    }

    stage('Result') {
      steps {
        emailext(subject: 'PF-WebWorkStation Operated...', body: '${buildResult}', attachLog: true, compressLog: true, from: 'virnect.corp@gmail.com', to: 'delbert@virnect.com dave@virnect.com')
      }
    }

  }
}