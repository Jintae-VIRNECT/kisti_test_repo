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
            echo 'e'
          }
        }

        stage('Staging Branch') {
          steps {
            echo 'q'
          }
        }

        stage('Master Branch') {
          steps {
            echo 'w'
          }
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
        catchError() {
          sh 'docker stop pf-message && docker rm pf-message || true'
          sh 'docker run -p 8084:8084 -d --name=pf-message pf-message'
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