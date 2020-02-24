pipeline {
  agent any
  stages {
    stage('Pre-Build') {
      steps {
        echo 'Pre-Build Stage'
        catchError() {
          sh 'chmod +x ./gradlew'
          sh './gradlew clean'
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
              sh 'docker build -t pf-gateway:develop .'
            }

          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            catchError() {
              sh 'docker build -t $registry_server/pf-gateway:staging .'
            }

          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            catchError() {
              sh 'docker build -t $registry_server/pf-gateway .'
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
          sh 'docker stop pf-gateway && docker rm pf-gateway || true'
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
              sh 'docker run -p 8073:8073 -d --restart=always --name=pf-gateway pf-gateway:develop'
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
              sh 'docker run -p 8073:8073 -d --restart=always --name=pf-gateway $registry_server/pf-gateway:staging'
              sh 'docker push $registry_server/pf-gateway:staging'
              sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker pull \\${registry_server}/pf-gateway:staging", failOnError: true)
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker stop pf-gateway && docker rm pf-gateway || true", failOnError: true)
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker run -p 8073:8073 -d --restart=always --name=pf-gateway \\${registry_server}/pf-gateway:staging", failOnError: true)
            }

          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            catchError() {
              sh 'docker run -p 8073:8073 -d --restart=always --name=pf-gateway $registry_server/pf-gateway'
              sh 'docker push $registry_server/pf-gateway'
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