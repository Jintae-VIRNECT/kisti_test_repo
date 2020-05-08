pipeline {
  agent any
  stages {
    stage('Pre-Build') {
      steps {
        echo 'Pre-Build Stage'
        catchError() {
          sh 'npm cache verify'
          sh 'npm install'
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
          environment {
            NODE_ENV='develop'
          }
          steps {            
            sh 'npm run build:develop'
            sh 'docker build -t rm-web .'
          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          environment {
            NODE_ENV='develop'
          }
          steps {         
            sh 'npm run build'   
            sh 'docker build -t rm-web .'
          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          environment {
            NODE_ENV='production'
          }
          steps {           
            sh 'npm run build' 
            sh 'docker build -t rm-web .'
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
            sh 'count=`docker ps | grep rm-web | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop rm-web && docker rm rm-web; else echo "Not Running STOP&DELETE"; fi;'
            sh 'docker run -p 8886:8886 --restart=always -e "NODE_ENV=develop" -d --name=rm-web rm-web'
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
                  docker.image("rm-web").push("$GIT_COMMIT")
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
                          execCommand: "docker pull $aws_ecr_address/rm-web:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'count=`docker ps | grep rm-web| wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop rm-web && docker rm rm-web; else echo "Not Running STOP&DELETE"; fi;'
                        ),
                        sshTransfer(
                          execCommand: "docker run -p 8886:8886 --restart=always -e 'NODE_ENV=staging' -d --name=rm-web $aws_ecr_address/rm-web:\\${GIT_COMMIT}"
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
                  docker.image("rm-web").push("$GIT_COMMIT")
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
                          execCommand: "docker pull $aws_ecr_address/rm-web:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'count=`docker ps | grep rm-web| wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop rm-web && docker rm rm-web; else echo "Not Running STOP&DELETE"; fi;'
                        ),
                        sshTransfer(
                          execCommand: "docker run -p 8886:8886 --restart=always -e 'NODE_ENV=production' -d --name=rm-web $aws_ecr_address/rm-web:\\${GIT_COMMIT}"
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
      emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$remote')
      office365ConnectorSend 'https://outlook.office.com/webhook/41e17451-4a57-4a25-b280-60d2d81e3dc9@d70d3a32-a4b8-4ac8-93aa-8f353de411ef/JenkinsCI/e79d56c16a7944329557e6cb29184b32/d0ac2f62-c503-4802-8bf9-f6368d7f39f8'
    }
  }
}