pipeline {
  agent any
  stages {
    stage('Clean Old Artifacts') {
      steps {
        echo 'Clean Old Artifacts'
      }
    }

    stage('Gradle Wrapper Permission') {
      steps {
        sh 'chmod +x ./gradlew'
      }
    }

    stage('Clean Artifacts Directory') {
      steps {
        sh './gradlew clean'
      }
    }

    stage('Clean QueryDsl Directory') {
      steps {
        sh './gradlew cleanQuerydslSourcesDir'
      }
    }

    stage('Build') {
      steps {
        sh './gradlew build -x test'
      }
    }

    stage('Notify Build Step  Email') {
      steps {
        emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$DEFAULT_RECIPIENTS')
      }
    }

    stage('Dockerizing') {
      steps {
        echo 'Begin Dockerizing'
      }
    }

    stage('Build Dockerfile To Image') {
      steps {
        sh 'docker build -t pf-workspace/develop -f docker/Dockerfile.develop .'
      }
    }

    stage('Dockerizing Complete') {
      steps {
        echo 'Build Dockerfile to Image is Done.'
      }
    }

    stage('Notify Dockerizing Step Email') {
      steps {
        emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$DEFAULT_RECIPIENTS')
      }
    }

    stage('Delete Old Container') {
      steps {
        sh 'docker stop pf-workspace-develop && docker rm pf-workspace-develop || true'
      }
    }

    stage('Deploy') {
      steps {
        sh 'docker run -p 8082:8082 -d --name=pf-workspace-develop pf-workspace/develop'
      }
    }

    stage('Remove Old Image') {
      steps {
        sh 'docker rmi -f $(docker images -f "dangling=true" -q) || true'
      }
    }

    stage('Notify Deploy Step Email') {
      steps {
        emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$DEFAULT_RECIPIENTS')
      }
    }

  }
}