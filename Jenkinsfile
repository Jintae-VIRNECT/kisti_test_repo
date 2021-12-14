pipeline {
    agent any

    environment {
        GIT_TAG = sh(returnStdout: true, script: 'git for-each-ref refs/tags --sort=-creatordate --format="%(refname)" --count=1 | cut -d/  -f3').trim()
        REPO_NAME = sh(returnStdout: true, script: 'git config --get remote.origin.url | sed "s/.*:\\/\\/github.com\\///;s/.git$//"').trim()
    }

    stages {
        stage('Build') {
            parallel {/* Web Server Docker Based Jenkinsfile */
pipeline {
    agent any

    options {
        timeout(time: 2, unit: 'HOURS')
    }

    environment {
        REPO_URL = sh(returnStdout: true, script: 'git config --get remote.origin.url').trim()
        REPO_NAME = sh(returnStdout: true, script: 'git config --get remote.origin.url | rev | cut -f 1 -d "/" | rev | sed "s/.git//gi";sed "/^ *$/d"').toLowerCase().trim() 
        PORT = sh(returnStdout: true, script: 'cat docker/Dockerfile | egrep EXPOSE | awk \'{print $2}\'').trim()
        BRANCH_NAME = "${BRANCH_NAME.toLowerCase().trim()}"
        APP = ' '
        PREVIOUS_VERSION = sh(returnStdout: true, script: 'git semver get || git semver minor').trim()
        NEXT_VERSION = getNextSemanticVersion(majorPattern: '.*[Bb]REAKING CHANGE[;:].*', minorPattern: '.*[Ff]eat[;:].*', patchPattern: '.*[Ff]ix[;:].*').toString()
    }

    stages {
        stage ('checkout') {
            steps {
                checkout scm

                script {
                    result = sh(script: "git log -1 | grep 'chore: SOFTWARE VERSION UPDATED'", returnStatus: true)
                    if (result != 0) {
                        echo "performing build..."
                    } else {
                        echo "not running..."
                        echo 'clean up current directory'
                        deleteDir()
                        currentBuild.getRawBuild().getExecutor().interrupt(Result.SUCCESS)
                        sleep(1)
                    }                
                }
            }
        }

        stage ('sonarqube code analysis') {
            environment {
                scannerHome = tool 'sonarqube-scanner'
            }
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }                
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage ('edit package version') {
            steps {
                script {
                    if ("${BRANCH_NAME}" == 'master') {
                      sh '''
                        sed -i "/\\"version\\":/ c\\  \\"version\\": \\"${NEXT_VERSION}\\"," package.json
                      '''
                    } else {
                      sh '''
                        sed -i "/\\"version\\":/ c\\  \\"version\\": \\"${BRANCH_NAME}.${BUILD_NUMBER}\\"," package.json
                      '''
                    }
                }
            }
        }

        stage ('build docker image') {
            environment {
              ENV_NAME = "${env.BRANCH_NAME == "develop" ? "develop" : "production"}"
            }

            steps {
                script {
                    APP = docker.build("""${REPO_NAME}:${BRANCH_NAME}.${BUILD_NUMBER}""", """--build-arg NODE_ENV=${ENV_NAME} -f ./docker/Dockerfile .""")
                }
            }
            post {
                always {
                    jiraSendBuildInfo site: "${JIRA_URL}"
                }
            }
        }
        
        stage ('save image to nexus') {
            steps {
                script {
                    docker.withRegistry("""https://${NEXUS_REGISTRY}""", "jenkins_to_nexus") {
                        APP.push("${BRANCH_NAME}.${BUILD_NUMBER}")

                        if ("${BRANCH_NAME}" == 'master') {
                            APP.push("${NEXT_VERSION}")
                            APP.push("latest")
                        }
                    }
                }
            }
        }

        stage ('save image to ecr') {
            when { anyOf { branch 'master'; branch 'staging' } }
            steps {
                script {
                    docker.withRegistry("https://$aws_ecr_address", 'ecr:ap-northeast-2:aws-ecr-credentials') {
                        APP.push("${BRANCH_NAME}.${BUILD_NUMBER}")

                        if ("${BRANCH_NAME}" == 'master') {
                            APP.push("${NEXT_VERSION}")
                            APP.push("latest")
                        }
                    }
                }
            }
        }

        stage ('image scanning') {
            steps {
                writeFile file: 'anchore_images', text: "${NEXUS_REGISTRY}/${REPO_NAME}:${BRANCH_NAME}.${BUILD_NUMBER}"
                anchore name: 'anchore_images'
            }
        }

        
        stage ('deploy to development') {
            when {
                branch 'develop'
            }
                
            steps {
              sh '''
                docker login ${NEXUS_REGISTRY}
                docker pull ${NEXUS_REGISTRY}/${REPO_NAME}:${BRANCH_NAME}.${BUILD_NUMBER}
                
                count=`docker ps -a | grep ${REPO_NAME} | wc -l`

                if [ $count -eq 0 ]
                then 
                  echo "There are no running containers. Starting a new container..."
                  docker run --restart=on-failure:10 \
                    -d \
                    -e VIRNECT_ENV=develop \
                    -e CONFIG_SERVER=http://192.168.6.3:6383 \
                    -e WRITE_YOUR=ENVIRONMENT_VARIABLE_HERE \
                    -p ${PORT}:${PORT} \
                    --name=${REPO_NAME} ${NEXUS_REGISTRY}/${REPO_NAME}:${BRANCH_NAME}.${BUILD_NUMBER}
                else
                  echo "Found a running container. stop the running container..."
                  docker stop ${REPO_NAME} && docker rm ${REPO_NAME}

                  echo "Starting a new container..."
                  docker run --restart=on-failure:10 \
                    -d \
                    -e VIRNECT_ENV=develop \
                    -e CONFIG_SERVER=http://192.168.6.3:6383 \
                    -e WRITE_YOUR=ENVIRONMENT_VARIABLE_HERE \
                    -p ${PORT}:${PORT} \
                    --name=${REPO_NAME} ${NEXUS_REGISTRY}/${REPO_NAME}:${BRANCH_NAME}.${BUILD_NUMBER}
                fi
              '''
            }
                
            

            post {
                always {
                    jiraSendDeploymentInfo site: "${JIRA_URL}", environmentId: 'seoul-headquarters', environmentName: 'seoul-headquarters', environmentType: 'development'
                }
            }
        }

        stage ('deploy to staging') {
            when {
                branch 'staging'
            }
                
            steps {
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
                                        execCommand: "docker pull ${aws_ecr_address}/${REPO_NAME}:${BRANCH_NAME}.${BUILD_NUMBER}"
                                    ),
                                    sshTransfer(
                                        execCommand: """
                                            echo '${REPO_NAME} Container stop and delete'
                                            docker stop ${REPO_NAME} && docker rm ${REPO_NAME} 

                                            echo '${REPO_NAME} New Container start'
                                            docker run --restart=on-failure:10 \
                                                    -d \
                                                    -e VIRNECT_ENV=staging \
                                                    -e CONFIG_SERVER=https://stgconfig.virnect.com \
                                                    -e WRITE_YOUR=ENVIRONMENT_VARIABLE_HERE \
                                                    -p ${PORT}:${PORT} \
                                                    --name=${REPO_NAME} ${aws_ecr_address}/${REPO_NAME}:${BRANCH_NAME}.${BUILD_NUMBER}
                                        """
                                    )
                                ]
                            )
                        ]
                    )
                }
            }
            
            post {
                always {
                    jiraSendDeploymentInfo site: "${JIRA_URL}", environmentId: 'seoul-stg', environmentName: 'seoul-stg', environmentType: 'staging'
                }
            }
        }

        stage ('git push and create release on github') {
            when { branch 'master'; expression { env.PREVIOUS_VERSION != env.NEXT_VERSION } }
            steps {
                script {
                    withCredentials([string(credentialsId: 'github_api_access_token', variable: 'TOKEN')]) {
                        sh '''
                            git add package.json
                            git commit -m "chore: SOFTWARE VERSION UPDATED"
                            git push https://$TOKEN@github.com/virnect-corp/$REPO_NAME.git
                        '''

                        env.CHANGE_LOG = gitChangelog returnType: 'STRING', 
                            from: [type: 'REF', value: "${PREVIOUS_VERSION}"],
                            to: [type: 'REF', value: 'HEAD'],
                            template: "{{#tags}}{{#ifContainsBreaking commits}}### Breaking Changes \\r\\n {{#commits}}{{#ifCommitBreaking .}}{{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}}{{{commitDescription .}}}([{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}})) \\r\\n {{/ifCommitBreaking}}{{/commits}}{{/ifContainsBreaking}} {{#ifContainsType commits type='feat'}} ### Features \\r\\n {{#commits}}{{#ifCommitType . type='feat'}}{{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}}{{{commitDescription .}}}([{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}})) \\r\\n {{/ifCommitType}}{{/commits}}{{/ifContainsType}} {{#ifContainsType commits type='fix'}}### Bug Fixes \\r\\n {{#commits}}{{#ifCommitType . type='fix'}}{{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}}{{{commitDescription .}}}([{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}})) \\r\\n {{/ifCommitType}}{{/commits}}{{/ifContainsType}} \\r\\n Copyright (C) 2020, VIRNECT CO., LTD. - All Rights Reserved \\r\\n {{/tags}}"
                        
                        sh '''
                            curl \
                                -X POST \
                                -H "Accept: application/vnd.github.manifold-preview" \
                                -H "Authorization: token $TOKEN" \
                                -H "Content-Type: application/json" \
                                https://api.github.com/repos/virnect-corp/$REPO_NAME/releases \
                                -d '{"tag_name": "'"${NEXT_VERSION}"'", "target_commitish": "master", "name": "'"$NEXT_VERSION"'", "draft": false, "prerelease": false, "body": "'"$CHANGE_LOG"'"}'
                        '''
                    }
                }
            }
        }

        stage ('deploy to production') {
            when {
                branch 'master'
            }
                
            steps {
                script {
                    // pull and run container
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
                                        execCommand: "docker pull ${aws_ecr_address}/${REPO_NAME}:\\${NEXT_VERSION}"
                                    ),
                                    sshTransfer(
                                        execCommand: """
                                            echo '${REPO_NAME} Container stop and delete'
                                            docker stop ${REPO_NAME} && docker rm ${REPO_NAME} 

                                            echo '${REPO_NAME} New Container start'
                                            docker run --restart=on-failure:10 \
                                                -d \
                                                -e VIRNECT_ENV=production \
                                                -e CONFIG_SERVER=https://config.virnect.com \
                                                -e WRITE_YOUR=ENVIRONMENT_VARIABLE_HERE \
                                                -p ${PORT}:${PORT} \
                                                --name=${REPO_NAME} ${aws_ecr_address}/${REPO_NAME}:${NEXT_VERSION}
                                        """
                                    )
                                ]
                            )
                        ]
                    )
                }
            }                

            post {
                always {
                    jiraSendDeploymentInfo site: "${JIRA_URL}", environmentId: 'seoul-prod', environmentName: 'seoul-prod', environmentType: 'production'
                }
            }
        }
    }

    post {
        cleanup {
            echo 'clean up current directory'
            deleteDir()
        }
    }
}
                stage('Develop Branch') {
                    when {
                        branch 'develop'
                    }
                    steps {
                        sh 'docker build -t pf-login --build-arg NODE_ENV=develop -f docker/Dockerfile --build-arg "NPM_TOKEN=sfKAhE8K0ypM8ePwY2mUiR7k2jcOs/vXGFwD+eHwMZE=" .'
                    }
                }

                stage('Staging Branch') {
                    when {
                        branch 'staging'
                    }
                    steps {
                        sh 'git checkout ${GIT_TAG}'
                        sh 'docker build -t pf-login:${GIT_TAG} --build-arg NODE_ENV=production -f docker/Dockerfile .'
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
                        sh 'count=`docker ps -a | grep pf-login | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-login && docker rm pf-login; else echo "Not Running STOP&DELETE"; fi;'
                        sh 'docker run -p 8883:8883 --restart=always -e "CONFIG_SERVER=http://192.168.6.3:6383" -e "VIRNECT_ENV=develop" -e eureka.instance.ip-address=`hostname -I | awk \'{print $1}\'` -d --name=pf-login pf-login'
                        sh 'count=`docker ps -a | grep pf-login-onpremise | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-login-onpremise && docker rm pf-login-onpremise; else echo "Not Running STOP&DELETE"; fi;'
                        sh 'docker run -p 18883:8883 --restart=always -e "CONFIG_SERVER=http://192.168.6.3:6383" -e "VIRNECT_ENV=onpremise" -e eureka.instance.ip-address=`hostname -I | awk \'{print $1}\'` -d --name=pf-login-onpremise pf-login'
                        catchError {
                             sh "if [ `docker images | grep pf-login | grep -v 103505534696 | grep -v server | wc -l` -gt 3 ]; then docker rmi  -f \$(docker images | grep \"pf-login\" | grep -v server | grep -v \\${GIT_TAG} | grep -v \"latest\" | awk \'{print \$3}\'); else echo \"Just One Images...\"; fi;"
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
                                docker.image("pf-login:${GIT_TAG}").push("${GIT_TAG}")
                                docker.image("pf-login:${GIT_TAG}").push("latest")
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
                                                execCommand: "docker pull $aws_ecr_address/pf-login:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'count=`docker ps -a | grep pf-login| wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-login && docker rm pf-login; else echo "Not Running STOP&DELETE"; fi;'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker run -p 8883:8883 --restart=always -e 'CONFIG_SERVER=https://stgconfig.virnect.com' -e 'VIRNECT_ENV=staging' -e eureka.instance.ip-address=`hostname -I | awk \'{print \$1}\'` -d --name=pf-login $aws_ecr_address/pf-login:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: "if [ `docker images | grep pf-login | grep -v server | wc -l` -ne 1 ]; then docker rmi  -f \$(docker images | grep \"pf-login\" | grep -v server | grep -v \\${GIT_TAG} | awk \'{print \$3}\'); else echo \"Just One Images...\"; fi;"
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
                                                execCommand: "docker pull $aws_ecr_address/pf-login:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'count=`docker ps -a | grep pf-login| wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-login && docker rm pf-login; else echo "Not Running STOP&DELETE"; fi;'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker run -p 8883:8883 --restart=always -e 'CONFIG_SERVER=http://3.35.50.181:6383' -e 'VIRNECT_ENV=onpremise' -e eureka.instance.ip-address=`hostname -I | awk \'{print \$1}\'` -d --name=pf-login $aws_ecr_address/pf-login:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: "if [ `docker images | grep pf-login | grep -v server | wc -l` -ne 1 ]; then docker rmi  -f \$(docker images | grep \"pf-login\" | grep -v server | grep -v \\${GIT_TAG} | awk \'{print \$3}\'); else echo \"Just One Images...\"; fi;"
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
                                                execCommand: "docker pull $aws_ecr_address/pf-login:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'count=`docker ps -a | grep pf-login| wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-login && docker rm pf-login; else echo "Not Running STOP&DELETE"; fi;'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker run -p 8883:8883 --restart=always -e 'CONFIG_SERVER=https://config.virnect.com'  -e 'VIRNECT_ENV=production' -e eureka.instance.ip-address=`hostname -I | awk \'{print \$1}\'` -d --name=pf-login $aws_ecr_address/pf-login:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: "if [ `docker images | grep pf-login | grep -v server | wc -l` -ne 1 ]; then docker rmi  -f \$(docker images | grep \"pf-login\" | grep -v server | grep -v \\${GIT_TAG} | awk \'{print \$3}\'); else echo \"Just One Images...\"; fi;"
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
            emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$platform')
            office365ConnectorSend webhookUrl:'https://virtualconnect.webhook.office.com/webhookb2/9b126938-3d1f-4493-98bb-33f25285af65@d70d3a32-a4b8-4ac8-93aa-8f353de411ef/IncomingWebhook/72710a45ecce45e4bf72663717e7f323/d5a8ebb7-7fe2-4cd2-817c-1884fd25e7b0'
         }
    }
}
