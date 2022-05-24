/* Nodejs Web Server Docker Based Jenkinsfile */
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

        stage ('check new tag') {
            when { allOf { branch 'master'; buildingTag() } }
            steps {
                script {
                    echo "version updated. not running..."
                    deleteDir()
                    currentBuild.getRawBuild().getExecutor().interrupt(Result.SUCCESS)
                    sleep(1)
                }
            }
        }

        stage ('compatibility check') {
            when { anyOf { branch 'staging';branch 'develop'} }
            environment {
                REBASED_FROM_MASTER  = sh(script: "git branch --contains ${PREVIOUS_VERSION} | grep ${BRANCH_NAME}", returnStatus: true)
            }
            steps {
                script {
                    echo """
                    LATEST RELEASE VERSION: ${PREVIOUS_VERSION} \n
                    NEXT VERSION: ${NEXT_VERSION} \n
                    """
                    if (env.REBASED_FROM_MASTER != '0') {
                        echo """버전 호환이 맞지 않습니다. 아래 명령어를 통해 Rebase Merge 후 다시 시도해 주세요. \n
                            git rabase origin/master \n
                            git push -f origin ${BRANCH_NAME} \n
                        """
                    }
                }
            }
        }

        stage ('coverage analysis') {
            when { anyOf { branch 'staging'; branch 'develop'} }
            agent {
                docker { 
                    image '${NEXUS_REGISTRY}/jest-build-image:latest'
                    args '-v $WORKSPACE:/project -w /project'
                    reuseNode true
                }
            }
            steps {
                script {
                    try {
                        sh '''
                            yarn install
                            yarn test:coverage
                        '''
                    } catch (e) {}
                }
            }
        }

        stage ('sonarqube code analysis') {
            when { anyOf { branch 'staging'; branch 'develop'; } }
            environment { scannerHome = tool 'sonarqube-scanner' }
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }                
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage ('build docker image') {
            when { anyOf { branch 'master'; branch 'staging'; branch 'freezing'; branch 'develop'} }
            environment {
                DOCKER_BUILDKIT='1'
            }

            steps {
                script {
                    def APP_VERSION = '';

                    if ("${BRANCH_NAME}" == 'master') {
                        APP_VERSION = "${NEXT_VERSION}";
                    } else {
                        APP_VERSION = "${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}";
                    }

                    APP = docker.build("""${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}""", """--build-arg APP_VERSION=${APP_VERSION} -f ./docker/Dockerfile .""")
                }
            }
            post {
                always {
                    jiraSendBuildInfo site: "${JIRA_URL}"
                }
            }
        }
/*
        stage ('image scanning') {
            when { anyOf { branch 'staging'; branch 'develop'; } }
            steps {
                writeFile file: 'anchore_images', text: "${NEXUS_REGISTRY}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}"
                anchore name: 'anchore_images'
            }
        }
 */
        stage ('save image to nexus') {
            when { anyOf { branch 'master'; branch 'staging'; branch 'freezing'; branch 'develop'} }
            steps {
                script {
                    docker.withRegistry("""https://${NEXUS_REGISTRY}""", "jenkins_to_nexus") {
                        APP.push("${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}")

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
                        APP.push("${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}")

                        if ("${BRANCH_NAME}" == 'master') {
                            APP.push("${NEXT_VERSION}")
                            APP.push("latest")
                        }
                    }
                }
            }
        }

        stage ('deployment') {
            parallel {
                stage ('develop') {
                    when {
                        branch 'develop'
                    }
                    steps {
                        script {
                            withCredentials([usernamePassword(credentialsId: 'server_credentials', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                                def remote = [:]
                                remote.name = "${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}" 
                                remote.host = "${DEV_RENEWAL_SERVER}"
                                remote.allowAnyHosts = true 
                                remote.user = USERNAME 
                                remote.password = PASSWORD
                                remote.failOnError = true

                                sshCommand remote: remote, command: """
                                    docker login ${NEXUS_REGISTRY}
                                    docker pull ${NEXUS_REGISTRY}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}
                                    docker stop ${REPO_NAME} && docker rm ${REPO_NAME} || true
                                    docker run --restart=on-failure:10 \
                                        -d \
                                        -e VIRNECT_ENV=develop \
                                        -e CONFIG_SERVER=${DEV_CONFIG_SERVER} \
                                        -p ${PORT}:${PORT} \
                                        --name=${REPO_NAME} ${NEXUS_REGISTRY}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}
                                """
                            }
                        }
                    }
        /*
                        // onpremise
                        script {
                            withCredentials([usernamePassword(credentialsId: 'server_credentials', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                                def remote = [:]
                                remote.name = "${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}" 
                                remote.host = "${DEV_ONPREMISE_SERVER}"
                                remote.allowAnyHosts = true 
                                remote.user = USERNAME 
                                remote.password = PASSWORD
                                remote.failOnError = true

                                sshCommand remote: remote, command: """
                                    docker login ${NEXUS_REGISTRY}
                                    docker pull ${NEXUS_REGISTRY}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}
                                    docker stop ${REPO_NAME} && docker rm ${REPO_NAME} || true
                                    docker run --restart=on-failure:10 \
                                        -d \
                                        -e VIRNECT_ENV=onpremise \
                                        -e CONFIG_SERVER=${DEV_CONFIG_SERVER} \
                                        -p ${PORT}:${PORT} \
                                        --name=${REPO_NAME} ${NEXUS_REGISTRY}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}
                                """
                            }
                        }
        */
                    

                    post {
                        always {
                            jiraSendDeploymentInfo site: "${JIRA_URL}", environmentId: 'harington-development', environmentName: 'harington-development', environmentType: 'development'
        //                   jiraSendDeploymentInfo site: "${JIRA_URL}", environmentId: 'harington-development-onpremise', environmentName: 'harington-development-onpremise', environmentType: 'development'
                            }
                        }
                    }

                stage ('freezing') {
                    when {
                        branch 'freezing'
                    }
                        
                    steps {
                        script {
                            withCredentials([usernamePassword(credentialsId: 'server_credentials', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                                def remote = [:]
                                remote.name = "${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}" 
                                remote.host = "${DEV_FREEZING_SERVER}"
                                remote.allowAnyHosts = true 
                                remote.user = USERNAME 
                                remote.password = PASSWORD
                                remote.failOnError = true

                                sshCommand remote: remote, command: """
                                    docker login ${NEXUS_REGISTRY}
                                    docker pull ${NEXUS_REGISTRY}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}
                                    docker stop ${REPO_NAME} && docker rm ${REPO_NAME} || true
                                    docker run --restart=on-failure:10 \
                                        -d \
                                        -e VIRNECT_ENV=freezing \
                                        -e CONFIG_SERVER=${DEV_CONFIG_SERVER} \
                                        -p ${PORT}:${PORT} \
                                        --name=${REPO_NAME} ${NEXUS_REGISTRY}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}
                                """
                            }
                        }
                    }            

                    post {
                        always {
                            jiraSendDeploymentInfo site: "${JIRA_URL}", environmentId: 'harington-freezing', environmentName: 'harington-freezing', environmentType: 'development'
                        }
                    }
                }

                stage ('staging') {
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
                                                execCommand: "docker pull ${aws_ecr_address}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}"
                                            ),
                                            sshTransfer(
                                                execCommand: """
                                                    echo '${REPO_NAME} Container stop and delete'
                                                    docker stop ${REPO_NAME} && docker rm ${REPO_NAME} 

                                                    echo '${REPO_NAME} New Container start'
                                                    docker run --restart=on-failure:10 \
                                                            -d \
                                                            -e VIRNECT_ENV=staging \
                                                            -e CONFIG_SERVER=${STG_CONFIG_SERVER} \
                                                            -e WRITE_YOUR=ENVIRONMENT_VARIABLE_HERE \
                                                            -e eureka.instance.ip-address=`hostname -I | awk  \'{print \$1}\'` \
                                                            -p ${PORT}:${PORT} \
                                                            --name=${REPO_NAME} ${aws_ecr_address}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}
                                                """
                                            )
                                        ]
                                    )
                                ]
                            )
                        }
                    }
        /*
                        // onpremise
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
                                                execCommand: "docker pull ${aws_ecr_address}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}"
                                            ),
                                            sshTransfer(
                                                execCommand: """
                                                    echo '${REPO_NAME} Container stop and delete'
                                                    docker stop ${REPO_NAME} && docker rm ${REPO_NAME} 

                                                    echo '${REPO_NAME} New Container start'
                                                    docker run --restart=on-failure:10 \
                                                            -d \
                                                            -e VIRNECT_ENV=onpremise \
                                                            -e CONFIG_SERVER=${STG_ONPRE_CONFIG_SERVER} \
                                                            -e WRITE_YOUR=ENVIRONMENT_VARIABLE_HERE \
                                                            -p ${PORT}:${PORT} \
                                                            --name=${REPO_NAME} ${aws_ecr_address}/${REPO_NAME}:${NEXT_VERSION}-${BRANCH_NAME}-${BUILD_NUMBER}
                                                """
                                            )
                                        ]
                                    )
                                ]
                            )
                        }
        */
                    
                    
                    post {
                        always {
                            jiraSendDeploymentInfo site: "${JIRA_URL}", environmentId: 'aws-stging', environmentName: 'aws-stging', environmentType: 'staging'
        //                    jiraSendDeploymentInfo site: "${JIRA_URL}", environmentId: 'aws-stging-onpremise', environmentName: 'aws-stging-onpremise', environmentType: 'staging'
                            }
                        }
                    }
                

                stage ('production') {
                    when {
                        branch 'master'
                    }
                        
                    steps {
                        script {
                            echo "deploy production"

                            // pull and run container
                            sshPublisher(
                                continueOnError: false, failOnError: true,
                                publishers: [
                                    sshPublisherDesc(
                                        configName: 'aws-eu-deploy-prod',
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
                                                    docker stop ${REPO_NAME} && docker rm ${REPO_NAME} || true

                                                    echo '${REPO_NAME} New Container start'
                                                    docker run --restart=on-failure:10 \
                                                        -d \
                                                        -e VIRNECT_ENV=production \
                                                        -e CONFIG_SERVER=${EU_CONFIG_SERVER} \
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
                            jiraSendDeploymentInfo site: "${JIRA_URL}", environmentId: 'aws-production', environmentName: 'aws-production', environmentType: 'production'
                        }
                    }
                }
            }
        }
    
            
                
    stage ('release') {
        when { allOf { branch 'master'; expression { env.PREVIOUS_VERSION != env.NEXT_VERSION } } }
        steps {
            script {
                withCredentials([string(credentialsId: 'github_api_access_token', variable: 'TOKEN')]) {
                    sh '''
                        sed -i "/\\"version\\":/ c\\  \\"version\\": \\"${NEXT_VERSION}\\"," package.json
                        git add package.json
                        git commit -m "chore: SOFTWARE VERSION UPDATED"
                        git push https://$TOKEN@github.com/virnect-corp/$REPO_NAME.git
                    '''

                    env.CHANGE_LOG = gitChangelog returnType: 'STRING', 
                        from: [type: 'REF', value: "${PREVIOUS_VERSION}"],
                        to: [type: 'REF', value: 'master'],
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
    }

    post {
        success {
            slackSend (channel: env.SLACK_CHANNEL, color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
        failure {
            slackSend (channel: env.SLACK_CHANNEL, color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
        aborted {
            slackSend (channel: env.SLACK_CHANNEL, color: '#808080', message: "ABORTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }

        cleanup {
            echo 'clean up current directory'
            deleteDir()
        }
    }
}
