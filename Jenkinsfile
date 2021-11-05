pipeline {
    agent any

    environment {
        GIT_TAG = sh(returnStdout: true, script: 'git for-each-ref refs/tags --sort=-creatordate --format="%(refname)" --count=1 | cut -d/  -f3').trim()
        REPO_NAME = sh(returnStdout: true, script: 'git config --get remote.origin.url | sed "s/.*:\\/\\/github.com\\///;s/.git$//"').trim()
        SERVICE_NAME = sh(returnStdout: true, script: 'git config --get remote.origin.url | cut -d / -f5 | cut -d . -f1').trim()
    }

    stages {
        stage('Build') {
            parallel {
                stage('Develop Branch') {
                    when {
                        branch 'develop'
                    }
                    steps {
                        sh 'docker build -t pf-account -f docker/Dockerfile .'
                    }
                }

                stage('Staging Branch') {
                    when {
                        branch 'staging'
                    }
                    steps {
                        sh 'git checkout ${GIT_TAG}'
                        sh 'docker build -t pf-account:${GIT_TAG} -f docker/Dockerfile .'
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
                        branch 'develop'
                    }
                    steps {
                        sh 'wget http://localhost:8322/v2/api-docs -O /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_old.json'
                        sh 'count=`docker ps -a | grep pf-account | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-account && docker rm pf-account; else echo "Not Running STOP&DELETE"; fi;'
                        sh 'docker run -p 8322:8322 --restart=always  -e "CONFIG_SERVER=http://192.168.6.3:6383" -e "VIRNECT_ENV=develop" -d --name=pf-account pf-account'
                        sh 'count=`docker ps -a | grep pf-account-onpremise | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-account-onpremise && docker rm pf-account-onpremise; else echo "Not Running STOP&DELETE"; fi;'
                        sh 'docker run -p 18322:8322  --restart=always  -e "CONFIG_SERVER=http://192.168.6.3:6383" -e "VIRNECT_ENV=onpremise" -d --name=pf-account-onpremise pf-account'
                        sh 'wget http://localhost:8322/v2/api-docs -O /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_new.json'
                        sh '''
                           if [ `diff  /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_old.json   /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_new.json | wc -l` -gt 0 ];
                                then 
                                     echo "Swagger Changed."
                                     curl -H "Content-Type: application/json" --data  \'{"summary": "Swagger Change",  "sections" : [{ "facts": [{"name": "Swagger Service" ,"value": "\'"$REPO_NAME"\'"}, {"name": "Information","value": "\'"`java -jar /var/lib/jenkins/Swagger-Diff/Jar/swagger-diff.jar -old /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_old.json -new /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_new.json`"\'"}],"markdown": true}]}\'  \'https://virtualconnect.webhook.office.com/webhookb2/9b126938-3d1f-4493-98bb-33f25285af65@d70d3a32-a4b8-4ac8-93aa-8f353de411ef/IncomingWebhook/864150903f604b4a8c57ec558197ce45/d0ac2f62-c503-4802-8bf9-f6368d7f39f8\' 
                                     rm -f /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_old.json /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_new.json
                                else 
                                     echo "It has not changed."
                                     rm -f /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_old.json /var/lib/jenkins/Swagger-Diff/Diff/${SERVICE_NAME}_new.json
                            fi

                        '''
                        catchError {
                             sh "if [ `docker images | grep pf-account | grep -v 103505534696 | wc -l` -gt 3 ]; then docker rmi  -f \$(docker images | grep \"pf-account\" | grep -v \"latest\" | awk \'{print \$3}\'); else echo \"Just One Images...\"; fi;"
                        }
                    }
                }

                stage('Staging Branch') {
                    when {
                        branch 'staging'
                    }
                    steps {
                        script {
                            docker.withRegistry("https://$aws_ecr_address", 'ecr:ap-northeast-2:aws-ecr-credentials') {
                                docker.image("pf-account:${GIT_TAG}").push("${GIT_TAG}")
                                docker.image("pf-account:${GIT_TAG}").push("latest")
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
                                                execCommand: "docker pull $aws_ecr_address/pf-account:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'count=`docker ps -a | grep pf-account | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-account && docker rm pf-account; else echo "Not Running STOP&DELETE"; fi;'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker run -p 8322:8322 --restart=always -e 'CONFIG_SERVER=https://stgconfig.virnect.com' -e 'VIRNECT_ENV=staging' -e eureka.instance.ip-address=`hostname -I | awk  \'{print \$1}\'` -d --name=pf-account $aws_ecr_address/pf-account:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: "if [ `docker images | grep pf-account | wc -l` -ne 1 ]; then docker rmi  -f \$(docker images | grep \"pf-account\" | grep -v \\${GIT_TAG} | awk \'{print \$3}\'); else echo \"Just One Images...\"; fi;"
                                            )
                                        ]
                                    )
                                ]
                            )
                        }
                        script {
                            sshPublisher(
                                continueOnError: false, failOnError: true,
                                publishers: [
                                    sshPublisherDesc(
                                        configName: 'aws-onpremise-qa',
                                        verbose: true,
                                        transfers: [
                                            sshTransfer(
                                                execCommand: 'aws ecr get-login --region ap-northeast-2 --no-include-email | bash'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker pull $aws_ecr_address/pf-account:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'count=`docker ps -a | grep pf-account | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-account && docker rm pf-account; else echo "Not Running STOP&DELETE"; fi;'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker run -p 8322:8322 --restart=always -e 'CONFIG_SERVER=http://3.35.50.181:6383' -e 'VIRNECT_ENV=onpremise' -e eureka.instance.ip-address=`hostname -I | awk  \'{print \$1}\'` -d --name=pf-account $aws_ecr_address/pf-account:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: "if [ `docker images | grep pf-account | wc -l` -ne 1 ]; then docker rmi  -f \$(docker images | grep \"pf-account\" | grep -v \\${GIT_TAG} | awk \'{print \$3}\'); else echo \"Just One Images...\"; fi;"
                                            )
                                        ]
                                    )
                                ]
                            )
                        }
                        script {
                            def GIT_TAG_CONTENT = sh(returnStdout: true, script: 'git for-each-ref refs/tags/$GIT_TAG --format=\'%(contents)\' | sed -z \'s/\\\n/\\\\n/g\'')
                            def payload = """
                            {"tag_name": "$GIT_TAG", "name": "$GIT_TAG", "body": "$GIT_TAG_CONTENT", "target_commitish": "master", "draft": false, "prerelease": true}
                            """                             

                            sh "curl -d '$payload' -X POST 'https://api.github.com/repos/$REPO_NAME/releases?access_token=$securitykey'"
                            def GIT_TAG_RELEASE = sh(returnStdout: true, script: 'git for-each-ref refs/tags/$GIT_TAG --format=\'%(contents)\' | sed -z \'s/\\\n/\\\n\\\n/g\'')
                            sh "curl -H \"Content-Type: application/json\" --data '{\"summary\": \"GITHUB Release note\",\"sections\" : [{ \"facts\": [{\"name\": \"REPO_NAME\",\"value\": \"\'\"$REPO_NAME\"\'\"},{\"name\": \"TAG_VERSION\",\"value\": \"\'\"$GIT_TAG\"\'\"},{\"NAME\": \"Branch\",\"value\": \"Staging\"},{\"name\": \"Information\",\"value\": \"\'\"$GIT_TAG_RELEASE\"\'\"}],\"markdown\": true}]}' -X POST 'https://virtualconnect.webhook.office.com/webhookb2/41e17451-4a57-4a25-b280-60d2d81e3dc9@d70d3a32-a4b8-4ac8-93aa-8f353de411ef/IncomingWebhook/642d8eba6da64076aba68a6bb6cc3e96/d0ac2f62-c503-4802-8bf9-f6368d7f39f8'"

                        }
                    }
                }

                stage('Master Branch') {
                    when {
                        branch 'master'
                    }
                    steps {
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
                                                execCommand: "docker pull $aws_ecr_address/pf-account:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'count=`docker ps -a | grep pf-account | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-account && docker rm pf-account; else echo "Not Running STOP&DELETE"; fi;'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker run -p 8322:8322 --restart=always -e 'CONFIG_SERVER=https://config.virnect.com' -e 'VIRNECT_ENV=production' -e eureka.instance.ip-address=`hostname -I | awk  \'{print \$1}\'` -d --name=pf-account $aws_ecr_address/pf-account:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: "if [ `docker images | grep pf-account | wc -l` -ne 1 ]; then docker rmi  -f \$(docker images | grep \"pf-account\" | grep -v \\${GIT_TAG} | awk \'{print \$3}\'); else echo \"Just One Images...\"; fi;"
                                            )
                                        ]
                                    )
                                ]
                            )
                        }

                        script {
                            def GIT_RELEASE_INFO = sh(returnStdout: true, script: 'curl -X GET https:/api.github.com/repos/$REPO_NAME/releases/tags/$GIT_TAG?access_token=$securitykey')
                            def RELEASE = readJSON text: "$GIT_RELEASE_INFO"
                            def RELEASE_ID = RELEASE.id
                            def payload = """
                            {"prerelease": false}
                            """

                            sh "echo '$RELEASE'"

                            sh "curl -d '$payload' -X PATCH 'https://api.github.com/repos/$REPO_NAME/releases/$RELEASE_ID?access_token=$securitykey'"
                            def GIT_TAG_RELEASE = sh(returnStdout: true, script: 'git for-each-ref refs/tags/$GIT_TAG --format=\'%(contents)\' | sed -z \'s/\\\n/\\\n\\\n/g\'')
                            sh "curl -H \"Content-Type: application/json\" --data '{\"summary\": \"GITHUB Release note\",\"sections\" : [{ \"facts\": [{\"name\": \"REPO_NAME\",\"value\": \"\'\"$REPO_NAME\"\'\"},{\"name\": \"TAG_VERSION\",\"value\": \"\'\"$GIT_TAG\"\'\"},{\"NAME\": \"Branch\",\"value\": \"Master\"},{\"name\": \"Information\",\"value\": \"\'\"$GIT_TAG_RELEASE\"\'\"}],\"markdown\": true}]}' -X POST 'https://virtualconnect.webhook.office.com/webhookb2/41e17451-4a57-4a25-b280-60d2d81e3dc9@d70d3a32-a4b8-4ac8-93aa-8f353de411ef/IncomingWebhook/5433af0a21da48a799418f2c7a046d3d/d0ac2f62-c503-4802-8bf9-f6368d7f39f8'"

                        }
                    }
                }
            }
        }
    }

    post {
        always {
            office365ConnectorSend webhookUrl:'https://virtualconnect.webhook.office.com/webhookb2/9b126938-3d1f-4493-98bb-33f25285af65@d70d3a32-a4b8-4ac8-93aa-8f353de411ef/IncomingWebhook/72710a45ecce45e4bf72663717e7f323/d5a8ebb7-7fe2-4cd2-817c-1884fd25e7b0'
        }
    }
}
