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
                sh 'NODE_ENV=develop yarn workspace workstation build'
                sh 'docker build -t pf-webworkstation .'
              }
            }
    
            stage('Staging Branch') {
              when {
                branch 'staging'
              }
              steps {
                sh 'NODE_ENV=staging yarn workspace workstation build'
                sh 'docker build -t pf-webworkstation .'
              }
            }
    
            stage('Master Branch') {
              when {
                branch 'master'
              }
              steps {
                sh 'NODE_ENV=production yarn workspace workstation build'
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

   stage('Tunneling') {
      steps {
        echo 'SSH Check'
         catchError() {
          sh 'port=`netstat -lnp | grep 127.0.0.1:2122 | wc -l`; if [ ${port} -gt 0 ]; then echo "SSH QA Tunneling OK";else echo "SSH QA Tunneling Not OK";ssh -M -S Platform-QA -fnNT -L 2122:10.0.10.143:22 jenkins@13.125.24.98;fi'
          sh 'port=`netstat -lnp | grep 127.0.0.1:3122 | wc -l`; if [ ${port} -gt 0 ]; then echo "SSH Prod Tunneling OK";else echo "SSH Prod Tunneling Not OK";ssh -M -S Platform-Prod -fnNT -L 3122:10.0.20.170:22 jenkins@13.125.24.98;fi'
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
                sh 'count=`docker ps | grep pf-webworkstation | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-webworkstation && docker rm pf-webworkstation; else echo "Not Running STOP&DELETE"; fi;'
                sh 'docker run -p 8878:8878 --restart=always -e "NODE_ENV=develop" -d --name=pf-webworkstation pf-webworkstation'
                sh 'docker image prune -f'
              }
            }
    
            stage('Staging Branch') {
              when {
                branch 'staging'
              }

              steps {
            catchError() {
              script {
                docker.withRegistry("https://$aws_ecr_address", 'ecr:ap-northeast-2:aws-ecr-credentials') {
                  docker.image("pf-webworkstation").push("$GIT_COMMIT")
                }
              }

              script {
                sshPublisher(
                  continueOnError: false, failOnError: true,
                  publishers: [
                    sshPublisherDesc(
                      configName: 'aws-bastion-deploy-qa',
                      verbose: true,
                      transfers: [
                        sshTransfer(
                          execCommand: 'aws ecr get-login --region ap-northeast-2 --no-include-email | bash'
                        ),
                        sshTransfer(
                          execCommand: "docker pull $aws_ecr_address/pf-webworkstation:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'count=`docker ps | grep pf-webworkstation| wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-webworkstation && docker rm pf-webworkstation; else echo "Not Running STOP&DELETE"; fi;'
                        ),
                        sshTransfer(
                          execCommand: "docker run -p 8878:8878 --restart=always -e 'NODE_ENV=staging' -d --name=pf-webworkstation $aws_ecr_address/pf-webworkstation:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'docker image prune -f'
                        )
                      ]
                    )
                  ]
                )
              }

            }

          }
            }
    
            stage('Master Branch') {
              when {
                branch 'master'
              }

              steps {
              catchError() {
                script {
                  docker.withRegistry("https://$aws_ecr_address", 'ecr:ap-northeast-2:aws-ecr-credentials') {
                    docker.image("pf-webworkstation").push("$GIT_COMMIT")
                  }
                }

              script {
                sshPublisher(
                  continueOnError: false, failOnError: true,
                  publishers: [
                    sshPublisherDesc(
                      configName: 'aws-bastion-deploy-prod',
                      verbose: true,
                      transfers: [
                        sshTransfer(
                          execCommand: 'aws ecr get-login --region ap-northeast-2 --no-include-email | bash'
                        ),
                        sshTransfer(
                          execCommand: "docker pull $aws_ecr_address/pf-webworkstation:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'count=`docker ps | grep pf-webworkstation| wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-webworkstation && docker rm pf-webworkstation; else echo "Not Running STOP&DELETE"; fi;'
                        ),
                        sshTransfer(
                          execCommand: "docker run -p 8878:8878 --restart=always -e 'NODE_ENV=master' -d --name=pf-webworkstation $aws_ecr_address/pf-webworkstation:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'docker image prune -f'
                        )
                      ]
                    )
                  ]
                )
              }

            }

          }

            }
    
          }
        }
    
    
      }
      
      post {
        always {
          emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$platform')
        }
      }
    }
