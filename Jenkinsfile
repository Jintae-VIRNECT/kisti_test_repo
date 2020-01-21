pipeline {
  agent any
  stages {
    stage('Clean Old Artifacts') {
      steps {
        sh 'npm cache clean'
      }
    }

    stage('Build') {
      parallel {
        stage('Build Source') {
          steps {
            echo 'Install Package'
            sh 'npm install'
            sh 'npm run build:develop'
          }
        }

        stage('Notify Email') {
          steps {
            emailext(subject: 'RM-Web Operated...', attachLog: true, compressLog: true, body: 'Start Build...', from: 'virnect.corp@gmail.com', to: 'delbert@virnect.com wooka@virnect.com')
          }
        }

      }
    }

    stage('Dockerizing') {
      parallel {
        stage('Build Dockerfile') {
          steps {
            sh 'cp docker/Dockerfile.develop ./'
            sh 'docker build -t rm-web/develop -f docker/Dockerfile.develop .'
          }
        }

        stage('Delete Old Container') {
          steps {
            sh '''docker stop rm-web-develop || true
docker rm rm-web-develop || true'''
          }
        }

        stage('Notify Email') {
          steps {
            emailext(subject: 'RM-Web Operated..', body: 'Start Dockerizing...', attachLog: true, compressLog: true, to: 'delbert@virnect.com wooka@virnect.com')
          }
        }

      }
    }

    stage('Deploy') {
      parallel {
        stage('Deploy') {
          steps {
            sh 'docker run -p 8886:8886 -d --name rm-web-develop rm-web/develop'
          }
        }

        stage('Notify Email') {
          steps {
            emailext(subject: 'RM-Web Operated..', body: 'Start Deploy...', attachLog: true, compressLog: true, from: 'virnect.corp@gmail.com', to: 'delbert@virnect.com wooka@virnect.com')
          }
        }

      }
    }

  }
}