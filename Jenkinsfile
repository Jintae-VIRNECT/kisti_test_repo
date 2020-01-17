pipeline {
  agent any
  stages {
    stage('Clean Old Artifacts') {
      steps {
        echo 'Clean Old Artifacts'
      }
    }

    stage('Build') {
      parallel {
        stage('install package') {
          steps {
            echo 'Install Package'
            sh 'npm install'
          }
        }

        stage('build') {
          steps {
            sh 'yarn workspace smic build'
          }
        }

      }
    }

    stage('Dockerizing') {
      parallel {
        stage('Build Dockerfile') {
          steps {
            echo 'Begin Dockerizing'
            echo 'Build Dockerfile'
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

      }
    }

    stage('Deploy') {
      steps {
        echo 'Start Deploy'
        sh 'docker run -p 8887:8887 -d --name pf-webworkstation-develop pf-webworkstation/develop'
      }
    }

  }
}