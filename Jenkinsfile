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
            sh 'docker build -t pf-workspace:develop .'
          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            sh 'docker build -t pf-workspace:staging .'
          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            sh 'docker build -t pf-workspace .'
          }
        }

        stage('Test Branch') {
          when {
            branch 'test'
          }
          steps {
            sh 'docker build -t pf-workspace .'
          }
        }

      }
    }

    stage('Test') {
      steps {
        echo 'Test Stage'
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
            sh 'docker run -p 8082:8082 -e "SPRING_PROFILES_ACTIVE=develop" -d --name=pf-workspace pf-workspace:develop'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            sh 'docker run -p 8082:8082 -e "SPRING_PROFILES_ACTIVE=staging" -d --name=pf-workspace pf-workspace:staging'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            sh 'docker run -p 8082:8082 -e "SPRING_PROFILES_ACTIVE=master" -d --name=pf-workspace pf-workspace'
            sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
          }
        }

        stage('Test Branch') {
          when {
            branch 'test'
          }
          steps {
            catchError() {
              script {
                docker.withRegistry("https://$aws_ecr_address", 'ecr:ap-northeast-2:aws-ecr-credentials') {
                  docker.image("pf-workspace").push("$GIT_COMMIT")
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
                          execCommand: "docker pull $aws_ecr_address/pf-workspace:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'docker stop pf-workspace && docker rm pf-workspace'
                        ),
                        sshTransfer(
                          execCommand: "docker run -p 8082:8082 -e 'SPRING_PROFILES_ACTIVE=master' -d --restart=always --name=pf-workspace pf-workspace:\\${GIT_COMMIT}"
                        ),
                        sshTransfer(
                          execCommand: 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
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

    stage('Notify') {
      steps {
        emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$platform')
      }
    }

  }
}