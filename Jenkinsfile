pipeline {
  agent any
  stages {
    stage('Clean Old Artifacts') {
      steps {
        echo 'Clean Old Artifacts'
      }
    }

    stage('Install Package') {
      steps {
        echo 'Install Package'
        sh 'yarn'
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
            sh '''docker stop pf-webworkstation-develop
docker rm pf-webworkstation-develop'''
          }
        }

        stage('') {
          steps {
            echo 'Dockerizing Complete'
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