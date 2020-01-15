node('master') {
  def app

  try{
  notifyBuild('STARTED')

  stage('Checkout') {
    checkout scm
  }

	stage('Build origin source') {
		nodejs('Node 10.15.3 LTS') {
      sh label: '', script: 'npm install --global node-gyp'
      sh label: '', script: 'npm install'
			sh label: '', script: 'npm run build:production'
		}
	}

  stage('Build docker image') {
    app = docker.build("103505534696.dkr.ecr.ap-northeast-2.amazonaws.com/webclient")
  }

  stage('Push image to ECR') {
    docker.withRegistry('https://103505534696.dkr.ecr.ap-northeast-2.amazonaws.com/webclient', 'ecr:ap-northeast-2:remote_aws_admin') {
      app.push("latest")
    }
  }

  stage('Pull image from ECR and restart') {
     script {
      sshPublisher(
        continueOnError: false, failOnError: true,
        publishers: [
        sshPublisherDesc(
          configName: 'ec2-remote-server',
          verbose: true,
          transfers: [
          sshTransfer(
            execCommand: 'aws ecr get-login --region ap-northeast-2 --no-include-email | bash'
          ),
          sshTransfer(
            execCommand: "docker rm -f webclient && docker rmi 103505534696.dkr.ecr.ap-northeast-2.amazonaws.com/webclient:latest"
          ),
          sshTransfer(
            execCommand: "docker pull 103505534696.dkr.ecr.ap-northeast-2.amazonaws.com/webclient:latest"
          ),
          sshTransfer(
            execCommand: "docker run -d -p 8887:8887 --name webclient 103505534696.dkr.ecr.ap-northeast-2.amazonaws.com/webclient:latest"
          )
          ])
        ])
      }
  }

  stage('Remove Image') {
    sh label: '', script: 'docker rmi -f 103505534696.dkr.ecr.ap-northeast-2.amazonaws.com/webclient:latest'
  }

  } catch(e) {
    currentBuild.result = "FAILED"
  } finally {
    notifyBuild(currentBuild.result)
  }

}

def notifyBuild(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESSFUL'

  // Default values
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details =
  """<img src='http://drive.google.com/uc?export=view&id=1YN1M-_0Fm9TrpiC1NcQyfmS_5shgJnNy'/><br><p>${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p>"""

  emailext (
      subject: subject,
      body: details,
      to: '$DEFAULT_RECIPIENTS'
    )
}