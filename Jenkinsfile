pipeline {
  agent any
        environment {
        GIT_TAG = sh(returnStdout: true, script: 'git for-each-ref refs/tags --sort=-committerdate --format="%(refname)" --count=1 | cut -d/  -f3').trim()
        REPO_NAME = sh(returnStdout: true, script: 'git config --get remote.origin.url | sed "s/.*:\\/\\/github.com\\///;s/.git$//"').trim()
      }
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
            branch 'develop_regular'
          }
          environment {
            NODE_ENV='develop'
          }
          steps {
            sh 'npm run build'
            sh 'docker build -t rm-web:regular .'
          }
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
            branch 'develop_regular'
          }
          steps {
            sh 'count=`docker ps | grep rm-web-regular | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop rm-web-regular && docker rm rm-web-regular; else echo "Not Running STOP&DELETE"; fi;'
            sh 'docker run -p 8890:8890 --restart=always -e "NODE_ENV=develop" -d --name=rm-web-regular rm-web:regular'
            sh 'docker image prune -f'
          }
        }

      }
    }
  }
}
