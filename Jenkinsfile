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
              sh 'yarn workspace smic build'
              sh 'cp docker/Dockerfile.develop ./'
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
                sh 'docker build -t pf-webworkstation:develop .'
              }
            }
    
            stage('Staging Branch') {
              when {
                branch 'staging'
              }
              steps {
                sh 'docker build -t pf-webworkstation:staging .'
              }
            }
    
            stage('Master Branch') {
              when {
                branch 'master'
              }
              steps {
                sh 'docker build -t pf-webworkstation .'
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
              sh 'docker stop pf-webworkstation && docker rm pf-webworkstation || true'
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
                sh 'docker run -p 8887:8887 -d --name=pf-webworkstation pf-webworkstation:develop'
                sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
              }
            }
    
            stage('Staging Branch') {
              when {
                branch 'staging'
              }
              steps {
                sh 'docker run -p 8887:8887 -d --name=pf-webworkstation pf-webworkstation:staging'
                sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
              }
            }
    
            stage('Master Branch') {
              when {
                branch 'master'
              }
              steps {
                sh 'docker run -p 8887:8887 -d --name=pf-webworkstation pf-webworkstation'
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