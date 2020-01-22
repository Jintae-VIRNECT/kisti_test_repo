pipeline {
  agent any
  stages {
    stage('Clean Old Artifacts') {
      steps {
        echo 'Clean Old Artifacts'
        sh '''yarn cache clean
'''
      }
    }

    stage('Build') {
      parallel {
        stage('Build') {
          steps {
            echo 'Install Package'
          }
        }

        stage('Notify Email') {
          steps {
            emailext(attachLog: true, subject: 'WebWorkStation Operated..', body: 'Start Build...', compressLog: true, to: 'delbert@virnect.com dave@virnect.com', from: 'virnect.corp@gmail.com')
          }
        }

      }
    }

    stage('Dockerizing') {
      parallel {
        stage('Build Dockerfile') {
          steps {
            echo 'Begin Dockerizing'
            sh 'cp docker/Dockerfile.develop ./'
            sh 'docker build -t pf-webworkstation/develop -f docker/Dockerfile.develop .'
          }
        }

        stage('Delete Old Container') {
          steps {
            echo 'Delete Old Container'
            sh '''docker stop pf-webworkstation-develop || true
docker rm pf-webworkstation-develop || true'''
          }
        }

        stage('Notify Email') {
          steps {
            emailext(subject: 'WebWorkStation Operated...', body: 'Start Dockerizing...', attachLog: true, compressLog: true, from: 'virnect.corp@gmail.com', to: 'delbert@virnect.com dave@virnect.com')
          }
        }

      }
    }

    stage('Deploy') {
      parallel {
        stage('Deploy') {
          steps {
            echo 'Start Deploy'
            sh 'docker run -p 8887:8887 -d --name pf-webworkstation-develop pf-webworkstation/develop'
          }
        }

        stage('Notify Email') {
          steps {
            emailext(subject: 'WebWorkStation Operated...', body: 'Start Deploy...', attachLog: true, compressLog: true, to: 'delbert@virnect.com dave@virnect.com', from: 'virnect.corp@gmail.com')
          }
        }

      }
    }

    stage('Notify Email') {
      steps {
        catchError(catchInterruptions: true, message: 'asdfas', buildResult: 'dfasdfasdf') {
          emailext(subject: 'sadfasd', body: 'fasdfasdf', attachLog: true, compressLog: true, to: 'delbert@virnect.com')
        }

      }
    }

  }
}