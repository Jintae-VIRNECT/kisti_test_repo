pipeline {
      agent any
        environment {
          GIT_TAG = sh(returnStdout: true, script: 'git for-each-ref refs/tags --sort=-taggerdate --format="%(refname)" --count=1 | cut -d/  -f3').trim()
          REPO_NAME = sh(returnStdout: true, script: 'git config --get remote.origin.url | sed "s/.*:\\/\\/github.com\\///;s/.git$//"').trim()
        }
      stages {
        stage('Pre-Build') {
          steps {
            echo 'Pre-Build Stage'
            catchError() {
              sh 'cp coturn/Dockerfile ./'
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
                sh 'docker build -t rm-coturnserver .'
              }
            } 
    
            stage('Staging Branch') {
              when {
                branch 'staging'
              }
              steps {
                sh 'git checkout ${GIT_TAG}'
                sh 'cp coturn/Dockerfile.qa ./Dockerfile'
                sh 'docker build -t rm-coturnserver:${GIT_TAG} .'
              }
            }
    
            stage('Master Branch') {
              when {
                branch 'master'
              }
              steps {
                sh 'git checkout ${GIT_TAG}'
                sh 'cp coturn/Dockerfile.prod ./Dockerfile'
                sh 'docker build -t rm-coturnserver:${GIT_TAG} .'
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
          sh 'port=`netstat -lnp | grep 127.0.0.1:4122 | wc -l`; if [ ${port} -gt 0 ]; then echo "SSH QA Coturn Tunneling OK";else echo "SSH QA Coturn Tunneling Not OK";ssh -M -S Remote-Coturn-QA -fnNT -L 4122:10.0.10.26:22 jenkins@13.125.24.98;fi'
          sh 'port=`netstat -lnp | grep 127.0.0.1:6122 | wc -l`; if [ ${port} -gt 0 ]; then echo "SSH Prod Coturn Tunneling OK";else echo "SSH Prod Coturn Tunneling Not OK";ssh -M -S Remote-Coturn-Prod -fnNT -L 6122:10.0.20.20:22 jenkins@13.125.24.98;fi'

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
                sh 'count=`docker ps -a | grep rm-coturnserver | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop rm-coturnserver && docker rm rm-coturnserver; else echo "Not Running STOP&DELETE"; fi;'
                sh 'docker run -p 4443:4443 -p 3478:3478 -p 3478:3478/udp -p 5349:5349 -p 5349:5349/udp -p 50000-50100:50000-50100 -p 50000-50100:50000-50100/udp -e --external-ip=192.168.6.3 --restart=always -d --name=rm-coturnserver rm-coturnserver'
                sh 'docker image prune -a -f'
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
                      docker.image("rm-coturnserver:${GIT_TAG}").push("staging")
                }
              }

              script {
                sshPublisher(
                  continueOnError: false, failOnError: true,
                  publishers: [
                    sshPublisherDesc(
                      configName: 'aws-bastion-deploy-remote-coturn-qa',
                      verbose: true,
                      transfers: [
                        sshTransfer(
                          execCommand: 'aws ecr get-login --region ap-northeast-2 --no-include-email | bash'
                        ),
                        sshTransfer(
                          execCommand: "docker pull $aws_ecr_address/rm-coturnserver:\\${GIT_TAG}"
                        ),
                        sshTransfer(
                          execCommand: 'count=`docker ps -a | grep rm-coturnserver| wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop rm-coturnserver && docker rm rm-coturnserver; else echo "Not Running STOP&DELETE"; fi;'
                        ),
                        sshTransfer(
                          execCommand: "docker run -p 4443:4443 -p 3478:3478 -p 3478:3478/udp -p 5349:5349 -p 5349:5349/udp -p 50000-50100:50000-50100 -p 50000-50100:50000-50100/udp -e --external-ip=`curl -s http://169.254.169.254/latest/meta-data/public-ipv4` --restart=always  -d --name=rm-coturnserver $aws_ecr_address/rm-coturnserver:\\${GIT_TAG}"
                        ),
                        sshTransfer(
                          execCommand: 'docker image prune -a -f'
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
                        docker.image("rm-coturnserver:${GIT_TAG}").push("master")
                  }
                }

              script {
                sshPublisher(
                  continueOnError: false, failOnError: true,
                  publishers: [
                    sshPublisherDesc(
                      configName: 'aws-bastion-deploy-remote-coturn-prod',
                      verbose: true,
                      transfers: [
                        sshTransfer(
                          execCommand: 'aws ecr get-login --region ap-northeast-2 --no-include-email | bash'
                        ),
                        sshTransfer(
                          execCommand: "docker pull $aws_ecr_address/rm-coturnserver:\\${GIT_TAG}"
                        ),
                        sshTransfer(
                          execCommand: 'count=`docker ps -a | grep rm-coturnserver| wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop rm-coturnserver && docker rm rm-coturnserver; else echo "Not Running STOP&DELETE"; fi;'
                        ),
                        sshTransfer(
                          execCommand: "docker run -p 4443:4443 -p 3478:3478 -p 3478:3478/udp -p 5349:5349 -p 5349:5349/udp -p 50000-50100:50000-50100 -p 50000-50100:50000-50100/udp -e --external-ip=`curl -s http://169.254.169.254/latest/meta-data/public-ipv4` --restart=always  -d --name=rm-coturnserver $aws_ecr_address/rm-coturnserver:\\${GIT_TAG}"
                        ),
                        sshTransfer(
                          execCommand: 'docker image prune -a -f'
                        )
                      ]
                    )
                  ]
                )
              }
              script {
                 def GIT_TAG_CONTENT = sh(returnStdout: true, script: 'git for-each-ref refs/tags/$GIT_TAG --format=\'%(contents)\' | sed -z \'s/\\\n/\\\\n/g\'')
                 def payload = """
                {"tag_name": "$GIT_TAG", "name": "$GIT_TAG", "body": "$GIT_TAG_CONTENT", "target_commitish": "master", "draft": false, "prerelease": false}
                """                             

                sh "curl -d '$payload' 'https://api.github.com/repos/$REPO_NAME/releases?access_token=$securitykey'"
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
