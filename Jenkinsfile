pipeline {
  agent any
  
  environment {
    GIT_TAG = sh(returnStdout: true, script: 'git for-each-ref refs/tags --sort=-creatordate --format="%(refname)" --count=1 | cut -d/  -f3').trim()
    REPO_NAME = sh(returnStdout: true, script: 'git config --get remote.origin.url | sed "s/.*:\\/\\/github.com\\///;s/.git$//"').trim()
  }
  
  stages {
    stage('Build') {
      parallel {
        stage('Develop Branch') {
          when {
            branch 'develop_test'
          }
          steps {
            sh 'docker build -t rm-web-test --build-arg NODE_ENV=develop -f docker/Dockerfile .'
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
        stage('Develop Branch') {
          when {
            branch 'develop_test'
          }
          steps {
            sh 'count=`docker ps | grep rm-web-test | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop rm-web-test && docker rm rm-web-test; else echo "Not Running STOP&DELETE"; fi;'
            sh 'docker run -p 9886:8886 --restart=always -e "CONFIG_SERVER=http://192.168.6.3:6383" -e "VIRNECT_ENV=develop" -d --name=rm-web-test rm-web-test'
            catchError {
              sh "if [ `docker images | grep rm-web-test | grep -v 103505534696 | grep -v server | wc -l` -gt 2 ]; then docker rmi  -f \$(docker images | grep \"rm-web-test\" | grep -v server | grep -v \\${GIT_TAG} | grep -v \"latest\" | awk \'{print \$3}\'); else echo \"Just One Images...\"; fi;"
            }
          }  
        }
      }
    }
  }
}
