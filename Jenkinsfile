pipeline {
  agent any
    environment {
      GIT_TAG = sh(returnStdout: true, script: 'git for-each-ref refs/tags --sort=-taggerdate --format="%(refname)" --count=1 | cut -d/  -f3').trim()
      REPO_NAME = sh(returnStdout: true, script: 'git config --get remote.origin.url | sed "s/.*:\\/\\/github.com\\///;s/.git$//"').trim()
    }
  stages {
    stage('Pre-Build') {
     steps {
        echo 'Test Stage'
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
                sh 'docker build -t pf-rabbitmq .'
              }
            }

            stage('Staging Branch') {
              when {
                branch 'staging'
              }
              steps {
                sh 'docker build -t pf-rabbitmq .'
              }
            }

            stage('Master Branch') {
              when {
                branch 'master'
              }
              steps {
                sh 'docker build -t pf-rabbitmq .'
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
            sh 'count=`docker ps -a | grep pf-rabbitmq | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-rabbitmq && docker rm pf-rabbitmq; else echo "Not Running STOP&DELETE"; fi;'
			sh 'docker run -p 5672:5672 -p 15672:15672 -p 15674:15674 -d --name=pf-rabbitmq pf-rabbitmq'
            sh 'docker image prune -a -f'
          }
        }

        stage('Staging Branch') {
          when {
            branch 'staging'
          }
          steps {
            sh 'count=`docker ps -a | grep pf-rabbitmq | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-rabbitmq && docker rm pf-rabbitmq; else echo "Not Running STOP&DELETE"; fi;'
			sh 'docker run -p 5672:5672 -p 15672:15672 -p 15674:15674 -d --name=pf-rabbitmq pf-rabbitmq'
            sh 'docker image prune -a -f'
          }
        }

        stage('Master Branch') {
          when {
            branch 'master'
          }
          steps {
            sh 'count=`docker ps -a | grep pf-rabbitmq | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-rabbitmq && docker rm pf-rabbitmq; else echo "Not Running STOP&DELETE"; fi;'
			sh 'docker run -p 5672:5672 -p 15672:15672 -p 15674:15674 -d --name=pf-rabbitmq pf-rabbitmq'
            sh 'docker image prune -a -f'
          }
        }
      }
    }
  }
   post {
      always {
        emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$platform')
        office365ConnectorSend 'https://outlook.office.com/webhook/41e17451-4a57-4a25-b280-60d2d81e3dc9@d70d3a32-a4b8-4ac8-93aa-8f353de411ef/JenkinsCI/e79d56c16a7944329557e6cb29184b32/d0ac2f62-c503-4802-8bf9-f6368d7f39f8'
      }
    }

}
