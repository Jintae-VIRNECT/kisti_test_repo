pipeline {
      agent any
      stages {
        stage('Pre-Build') {
          steps {
            echo 'Pre-Build Stage'
            catchError() {
              sh 'yarn cache clean'
              sh '"echo <password> | sudo -S rm yarn.lock" || true'
              sh 'yarn install'
              sh 'yarn workspace account dev'
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
                sh 'docker build -t pf-webaccount:develop .'
              }
            }
    
            stage('Staging Branch') {
              when {
                branch 'staging'
              }
              steps {
                sh 'docker build -t pf-webaccount:staging .'
              }
            }
    
            stage('Master Branch') {
              when {
                branch 'master'
              }
              steps {
                sh 'docker build -t pf-webaccount .'
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
              sh 'docker stop pf-webaccount && docker rm pf-webaccount || true'
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
                sh 'docker run -p 8822:8822 -d --name=pf-webaccount pf-webaccount:develop'
                sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
              }
            }
    
            stage('Staging Branch') {
              when {
                branch 'staging'
              }
              steps {
                sh 'docker run -p 8822:8822 -d --name=pf-webaccount pf-webaccount:staging'
                sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
              }
            }
    
            stage('Master Branch') {
              when {
                branch 'master'
              }
              steps {
                sh 'docker run -p 8822:8822 -d --name=pf-webaccount pf-webaccount'
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