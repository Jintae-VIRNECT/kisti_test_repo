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
              sh 'docker build -t pf-contentsmanagement .'
            }

          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            catchError() {
              sh 'docker build -t pf-contentsmanagement .'
            }

          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            catchError() {
              sh 'docker build -t pf-contentsmanagement .'
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
          sh 'docker stop pf-contentsmanagement && docker rm pf-contentsmanagement || true'
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
              sh 'count=`docker ps | grep pf-contentsmanagement | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-contentsmanagement && docker rm pf-contentsmanagement; else echo "Not Running STOP&DELETE"; fi;'
              sh 'docker run -p 8078:8078 --restart=always -e "SPRING_PROFILES_ACTIVE=develop" -v /data/content/contentsmanagement:/usr/app/upload -d --name=pf-contentsmanagement pf-contentsmanagement'
              sh 'docker image prune -f'
            }

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
                  docker.image("pf-contentsmanagement").push("$GIT_COMMIT")
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
                          execCommand: "docker pull $aws_ecr_address/pf-contentsmanagement:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'count=`docker ps | grep pf-contentsmanagement | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-contentsmanagement && docker rm pf-contentsmanagement; else echo "Not Running STOP&DELETE"; fi;'
                        ),
                        sshTransfer(
                          execCommand: "docker run -p 8078:8078 --restart=always -e 'SPRING_PROFILES_ACTIVE=staging' -v /data/content/contentsmanagement:/usr/app/upload -d --name=pf-contentsmanagement $aws_ecr_address/pf-contentsmanagement:\\${GIT_COMMIT}"
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
                  docker.image("pf-contentsmanagement").push("$GIT_COMMIT")
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
                          execCommand: "docker pull $aws_ecr_address/pf-contentsmanagement:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'count=`docker ps | grep pf-contentsmanagement | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-contentsmanagement && docker rm pf-contentsmanagement; else echo "Not Running STOP&DELETE"; fi;'
                        ),
                        sshTransfer(
                          execCommand: "docker run -p 8078:8078 --restart=always -e 'SPRING_PROFILES_ACTIVE=production' -v /data/content/contentsmanagement:/usr/app/upload -d --name=pf-contentsmanagement $aws_ecr_address/pf-contentsmanagement:\\${GIT_COMMIT}"
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

    stage('Notify') {
      steps {
        emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$platform')
      }
    }

  }
}
