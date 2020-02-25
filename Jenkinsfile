pipeline {
  agent any
  stages {
    stage('Pre-Build') {
      steps {
        echo 'Pre-Build Stage'
        catchError() {
          sh 'npm cache verify'
          sh 'npm install'
          sh 'npm run build'
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
              sh 'docker build -t rm-web:develop .'
            }

          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            catchError() {
              sh 'docker build -t $registry_server/rm-web:staging .'
            }

          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            catchError() {
              sh 'docker build -t $registry_server/rm-web .'
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
          sh 'docker stop rm-web && docker rm rm-web || true'
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
              sh 'docker run -p 8886:8886 -d --restart=always --name=rm-web -e SSL_ENV=private -e NODE_ENV=develop rm-web:develop'
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
              sh 'docker run -p 8886:8886 -d --restart=always --name=rm-web -e SSL_ENV=private -e NODE_ENV=develop $registry_server/rm-web:staging'
              sh 'docker push $registry_server/rm-web:staging'
              sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker pull \\${registry_server}/rm-web:staging", failOnError: true)
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker stop rm-web && docker rm rm-web || true", failOnError: true)
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker run -p 8886:8886 -d --restart=always --name=rm-web \\${registry_server}/rm-web:staging", failOnError: true)
            }

          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            catchError() {
              sh 'docker run -p 8886:8886 -d --restart=always --name=rm-web -e SSL_ENV=public -e NODE_ENV=production $registry_server/rm-web'
              sh 'docker push $registry_server/rm-web'
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