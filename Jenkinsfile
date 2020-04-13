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
            catchError() {
              sh 'docker build -t pf-download:develop .'
            }

          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            catchError() {
              sh 'docker build -t $registry_server/pf-download:staging .'
            }

          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            catchError() {
              sh 'docker build -t $registry_server/pf-download .'
            }

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
          sh 'docker stop pf-download && docker rm pf-download || true'
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
            catchError() {
              sh 'docker run -p 8086:8086 -e "SPRING_PROFILES_ACTIVE=develop" -d --restart=always --name=pf-download pf-download:develop'
              sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
            }

          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }          
          

          steps {
            catchError() {
              sh 'docker run -p 8086:8086 -e "SPRING_PROFILES_ACTIVE=staging" -d --restart=always --name=pf-download $registry_server/pf-download:staging'
              sh 'docker push $registry_server/pf-download:staging'
              sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker pull \\${registry_server}/pf-download:staging", failOnError: true)
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker stop pf-download && docker rm pf-download || true", failOnError: true)
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker run -p 8086:8086 -d --restart=always --name=pf-download \\${registry_server}/pf-download:staging", failOnError: true)
            }

          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            catchError() {
              sh 'docker run -p 8086:8086 -e "SPRING_PROFILES_ACTIVE=master" -d --restart=always --name=pf-download $registry_server/pf-download'
              sh 'docker push $registry_server/pf-download'
              sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
            }

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
