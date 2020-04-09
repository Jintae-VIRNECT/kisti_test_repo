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
                        sh 'docker build -t pf-license .'
                    }
                }

                stage('Staging Branch') {
                    when {
                        branch 'staging'
                    }
                    steps {
                        sh 'docker build -t pf-license .'
                    }
                }

                stage('Master Branch') {
                    when {
                        branch 'master'
                    }
                    steps {
                        sh 'docker build -t pf-license .'
                    }
                }

            }
        }

        stage('Test') {
            steps {
                echo 'Test Stage'
            }
        }

        stage('Pre-Deploy') {
            steps {
                echo 'Pre-Deploy Stage'
                catchError() {
                    sh 'docker stop pf-license && docker rm pf-license || true'
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
                        sh 'count=`docker ps | grep pf-license | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-license && docker rm pf-license; else echo "Not Running STOP&DELETE"; fi;'
                        sh 'docker run -p 8632:8632 --restart=always -e "SPRING_PROFILES_ACTIVE=develop" -d --name=pf-license pf-license'
                        sh 'docker image prune -f'
                    }
                }

                stage('Staging Branch') {
                    when {
                        branch 'staging'
                    }
                    steps {
                        catchError() {
                            script {
                                docker.withRegistry("https://$aws_ecr_address", 'ecr:ap-northeast-2:aws-ecr-credentials') {
                                    docker.image("pf-license").push("$GIT_COMMIT")
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
                                                                        execCommand: "docker pull $aws_ecr_address/pf-license:\\${GIT_COMMIT}"
                                                                ),
                                                                sshTransfer(
                                                                        execCommand: 'count=`docker ps | grep pf-license | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-license && docker rm pf-license; else echo "Not Running STOP&DELETE"; fi;'
                                                                ),
                                                                sshTransfer(
                                                                        execCommand: "docker run -p 8632:8632 --restart=always -e 'SPRING_PROFILES_ACTIVE=staging' -d --name=pf-license $aws_ecr_address/pf-license:\\${GIT_COMMIT}"
                                                                ),
                                                                sshTransfer(
                                                                        execCommand: 'docker image prune -f'
                                                                )
                                                        ]
                                                )
                                        ]
                                )
                            }

                        }

                    }
                }


                stage('Master Branch') {
                    when {
                        branch 'master'
                    }
                    steps {
                        catchError() {
                            script {
                                docker.withRegistry("https://$aws_ecr_address", 'ecr:ap-northeast-2:aws-ecr-credentials') {
                                    docker.image("pf-license").push("$GIT_COMMIT")
                                }
                            }

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
                                                                        execCommand: "docker pull $aws_ecr_address/pf-license:\\${GIT_COMMIT}"
                                                                ),
                                                                sshTransfer(
                                                                        execCommand: 'count=`docker ps | grep pf-license | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-license && docker rm pf-license; else echo "Not Running STOP&DELETE"; fi;'
                                                                ),
                                                                sshTransfer(
                                                                        execCommand: "docker run -p 8632:8632 --restart=always -e 'SPRING_PROFILES_ACTIVE=production' -d --name=pf-license $aws_ecr_address/pf-license:\\${GIT_COMMIT}"
                                                                ),
                                                                sshTransfer(
                                                                        execCommand: 'docker image prune -f'
                                                                )
                                                        ]
                                                )
                                        ]
                                )
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
    }
}
