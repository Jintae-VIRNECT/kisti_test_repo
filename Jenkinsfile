/* SpringBoot API Server Docker Based Jenkinsfile */
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
        NEXT_VERSION = getNextSemanticVersion(to: [type: 'REF', value: 'HEAD'], patchPattern: '^[Ff]ix.*').toString()
        SLACK_CHANNEL = '#server_jenkins'
    }

    stages {
        stage ('start') {
            steps {
                slackSend (channel: env.SLACK_CHANNEL, color: '#FFFF00', message: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
            }
        }

        stage ('compatibility check') {
            when { anyOf { branch 'master'; branch 'staging'; branch 'develop'} }
            environment {
                IS_REBASE_MERGE_FROM_MASTER = sh(script: "git branch --contains ${PREVIOUS_VERSION} | grep ${BRANCH_NAME}", returnStatus: true)
            }
            steps {
                script {
                    echo """
                    LATEST RELEASE VERSION: ${PREVIOUS_VERSION} \n
                    NEXT VERSION: ${NEXT_VERSION} \n
                    """
                    if (env.IS_REBASE_MERGE_FROM_MASTER != '0') {
                        echo """버전 호환이 맞지 않습니다. 아래 명령어를 통해 Rebase Merge 후 다시 시도해 주세요. \n
                            git rabase origin/master \n
                            git push -f origin ${BRANCH_NAME} \n
                        """
                        
                        deleteDir()
                        currentBuild.getRawBuild().getExecutor().interrupt(Result.ABORTED)
                        sleep(1)
                    }
                }
            }
        }
        
        stage ('jacoco coverage analysis') {
            steps {
                sh '''
                    chmod +x ./gradlew && ./gradlew jacocoTestReport || IS_FAIL=true
                    if [[ $IS_FAIL = "true" ]]; then
                        echo "JaCoCo Report Failure"
                        exit 1
                    else
                        echo "JaCoCo Report Success"
                    fi
                    '''
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

        stage ('build docker image') {
            steps {
                script {
                    APP = docker.build("""${REPO_NAME}:${BRANCH_NAME}.${BUILD_NUMBER}""", "-f ./docker/Dockerfile .")
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
                        echo "Found a running container. Downloading old swagger api docs..."                        
                        wget http://localhost:${PORT}/v2/api-docs -O /var/lib/jenkins/Swagger-Diff/Diff/${REPO_NAME}_old.json

                        echo "stop the running container..."
                        docker stop ${REPO_NAME} && docker rm ${REPO_NAME}

                        echo "Starting a new container..."
                        docker run --restart=on-failure:10 \
                            -d \
                            -e VIRNECT_ENV=develop \
                            -e CONFIG_SERVER=http://192.168.6.3:6383 \
                            -e WRITE_YOUR=ENVIRONMENT_VARIABLE_HERE \
                            -p ${PORT}:${PORT} \
                            --name=${REPO_NAME} ${NEXUS_REGISTRY}/${REPO_NAME}:${BRANCH_NAME}.${BUILD_NUMBER}

                        sleep 3

                        echo "Downloading new swagger api docs..."
                        wget http://localhost:${PORT}/v2/api-docs -O /var/lib/jenkins/Swagger-Diff/Diff/${REPO_NAME}_new.json

                        echo "Running swagger diff..."
                        if [ `diff /var/lib/jenkins/Swagger-Diff/Diff/${REPO_NAME}_old.json /var/lib/jenkins/Swagger-Diff/Diff/${REPO_NAME}_new.json | wc -l` -gt 0 ];
                        then 
                            echo "Swagger Changed."
                            curl -H "Content-Type: application/json" --data \
                                '{ \
                                    "summary": "Swagger Change", \
                                    "sections" : [ \
                                        { \
                                            "facts": [ \
                                                { \
                                                    "name": "Swagger Service", \
                                                    "value": "'"$REPO_NAME"'" \
                                                }, \
                                                { \
                                                    "name": "Information", \
                                                    "value": "'"`java -jar /var/lib/jenkins/Swagger-Diff/Jar/swagger-diff.jar -old /var/lib/jenkins/Swagger-Diff/Diff/${REPO_NAME}_old.json -new /var/lib/jenkins/Swagger-Diff/Diff/${REPO_NAME}_new.json`"'" \
                                                } \
                                            ], \
                                            "markdown": true \
                                        } \
                                    ] \
                                }' ${TEAMS_SWAGGER_CHANNEL}
                        else 
                            echo "It has not changed."
                        fi
                        rm -f /var/lib/jenkins/Swagger-Diff/Diff/${REPO_NAME}_old.json /var/lib/jenkins/Swagger-Diff/Diff/${REPO_NAME}_new.json
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
                                                    -e eureka.instance.ip-address=`hostname -I | awk  \'{print \$1}\'` \
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

        stage ('create release on github') {
            when { branch 'master'; expression { env.PREVIOUS_VERSION != env.NEXT_VERSION } }
            steps {
                script {
                    env.CHANGE_LOG = gitChangelog returnType: 'STRING', 
                        from: [type: 'REF', value: "${PREVIOUS_VERSION}"],
                        to: [type: 'REF', value: 'master'],
                        template: "{{#tags}}{{#ifContainsBreaking commits}}### Breaking Changes \\r\\n {{#commits}}{{#ifCommitBreaking .}}{{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}}{{{commitDescription .}}}([{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}})) \\r\\n {{/ifCommitBreaking}}{{/commits}}{{/ifContainsBreaking}} {{#ifContainsType commits type='feat'}} ### Features \\r\\n {{#commits}}{{#ifCommitType . type='feat'}}{{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}}{{{commitDescription .}}}([{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}})) \\r\\n {{/ifCommitType}}{{/commits}}{{/ifContainsType}} {{#ifContainsType commits type='fix'}}### Bug Fixes \\r\\n {{#commits}}{{#ifCommitType . type='fix'}}{{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}}{{{commitDescription .}}}([{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}})) \\r\\n {{/ifCommitType}}{{/commits}}{{/ifContainsType}} \\r\\n Copyright (C) 2020, VIRNECT CO., LTD. - All Rights Reserved \\r\\n {{/tags}}"
                    
                    // create git tag on github
                    withCredentials([string(credentialsId: 'github_api_access_token', variable: 'TOKEN')]) {
                        sh '''
                        curl \
                            -X POST \
                            -H "Accept: application/vnd.github.manifold-preview" \
                            -H "Authorization: token $TOKEN" \
                            -H "Content-Type: application/json" \
                            https://api.github.com/repos/virnect-corp/$REPO_NAME/releases \
                            -d '{"tag_name": "'"${NEXT_VERSION}"'", "target_commitish": "'"$GIT_COMMIT"'", "name": "'"$NEXT_VERSION"'", "draft": false, "prerelease": false, "body": "'"$CHANGE_LOG"'"}'
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
                    //echo "deploy production"
                
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
                                                -e eureka.instance.ip-address=`hostname -I | awk  \'{print \$1}\'` \
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