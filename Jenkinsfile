pipeline {
  agent any
  stages {
    stage('Pre-Build') {
      steps {
        echo 'Pre-Build Stage'
        catchError() {
          sh 'chmod +x ./gradlew'
          sh './gradlew clean'
          sh './gradlew cleanQuerydslSourcesDir'
          sh './gradlew build -x test'
          sh 'cp docker/Dockerfile ./'
        }

      }
    }

    stage('Build') {
      parallel {
        stage('Build') {
          steps {
            echo 'Build Stage'
          }
        }

        stage('Develop Branch') {
          when {
            branch 'develop'
          }
          steps {
            sh 'docker build -t pf-processmanagement:develop .'
          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            sh 'docker build -t pf-processmanagement:staging .'
          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            sh 'docker build -t pf-processmanagement .'
          }
        }

      }
    }

    stage('Test') {
      steps {
        echo 'Test Stage'
      }
    }

    stage('Pre-Deploy') {
      steps {
        echo 'Pre-Deploy Stage'
        catchError() {
          sh 'docker stop pf-processmanagement && docker rm pf-processmanagement || true'
        }

      }
    }

    stage('Deploy') {
      parallel {
        stage('Deploy') {
          steps {
            echo 'Deploy Stage'
          }
        }

        stage('Develop Branch') {
          when {
            branch 'develop'
          }
          steps {
            sh 'docker run -p 8079:8079 -d --name=pf-processmanagement -v /data/content:/usr/app/upload pf-processmanagement:develop'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            sh 'docker run -p 8079:8079 -d --name=pf-processmanagement pf-processmanagement:staging'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            sh 'docker run -p 8079:8079 -d --name=pf-processmanagement pf-processmanagement'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
          }
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
