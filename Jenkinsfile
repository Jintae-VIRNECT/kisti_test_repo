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
            sh 'docker build -t pf-contentmanagement:develop .'
            catchError() {
              sh 'docker build -t pf-contentmanagement:develop .'
            }

          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            sh 'docker build -t pf-contentmanagement:staging .'
            catchError() {
              sh 'docker build -t $registry_server/pf-contentmanagement:staging .'
            }

          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            sh 'docker build -t pf-contentmanagement .'
            catchError() {
              sh 'docker build -t $registry_server/pf-contentmanagement .'
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
          sh 'docker stop pf-contentmanagement && docker rm pf-contentmanagement || true'
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
            sh 'docker run -p 8078:8078 -d -e SPRING_PROFILES_ACTIVE=develop --name=pf-contentmanagement -v /data/content:/usr/app/upload pf-contentmanagement:develop'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
            catchError() {
              sh 'docker run -p 8078:8078 -d -e SPRING_PROFILES_ACTIVE=develop --restart=always --name=pf-contentmanagement pf-contentmanagement:develop'
              sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
            }

          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          }


          steps {
            sh 'docker run -p 8078:8078 -d -e SPRING_PROFILES_ACTIVE=staging --name=pf-contentmanagement pf-contentmanagement:staging'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
            catchError() {
              sh 'docker run -p 8078:8078 -d -e SPRING_PROFILES_ACTIVE=staging --restart=always --name=pf-contentmanagement $registry_server/pf-contentmanagement:staging'
              sh 'docker push $registry_server/pf-contentmanagement:staging'
              sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker pull \\${registry_server}/pf-contentmanagement:staging", failOnError: true)
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker stop pf-contentmanagement && docker rm pf-contentmanagement || true", failOnError: true)
              sshCommand(remote: [allowAnyHosts: true, name: "${qa_server_name}", host:"${qa_server}", user:"${qa_server_user}", password:"${qa_server_password}"], command: "docker run -p 8078:8078 -d --restart=always --name=pf-contentmanagement \\${registry_server}/pf-contentmanagement:staging", failOnError: true)
            }

          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            sh 'docker run -p 8078:8078 -d -e SPRING_PROFILES_ACTIVE=production --name=pf-contentmanagement pf-contentmanagement'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
            catchError() {
              sh 'docker run -p 8078:8078 -d -e SPRING_PROFILES_ACTIVE=production --restart=always --name=pf-contentmanagement $registry_server/pf-contentmanagement'
              sh 'docker push $registry_server/pf-contentmanagement'
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