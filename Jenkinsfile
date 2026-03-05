pipeline {
    agent {
        label 'maven-docker-agent'
    }
    options {
        skipStagesAfterUnstable()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }
    triggers {
        pollSCM('H/5 * * * *')
    }
    stages {
        stage('Build & Test') {
            parallel {
                stage('instrument-service') {
                    when { changeset "services/instrument-service/**" }
                    steps {
                        dir('services/instrument-service') { 
                            sh 'mvn -B clean package' 
                        }
                    }
                    post {
                        always {
                            junit 'services/instrument-service/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('market-data-service') {
                    when { changeset "services/market-data-service/**" }
                    steps {
                        dir('services/market-data-service') { 
                            sh 'mvn -B clean package' 
                        }
                    }
                    post {
                        always {
                            junit 'services/market-data-service/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('portfolio-service') {
                    when { changeset "services/portfolio-service/**" }
                    steps {
                        dir('services/portfolio-service') {
                            sh 'mvn -B clean package' 
                        }
                    }
                    post {
                        always {
                            junit 'services/portfolio-service/target/surefire-reports/*.xml'
                        }
                    }
                }
            }
        }
        stage('Build Docker Images') {
            parallel {
                stage('instrument-service') {
                    when { changeset "services/instrument-service/**" }
                    steps {
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKERHUB_USER',
                            passwordVariable: 'DOCKERHUB_PASS'
                        )]) {
                            dir('services/instrument-service') {
                                sh "${WORKSPACE}/infra/jenkins/scripts/build_image.sh"
                            }
                        }
                    }
                }
                stage('market-data-service') {
                    when { changeset "services/market-data-service/**" }
                    steps {
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKERHUB_USER',
                            passwordVariable: 'DOCKERHUB_PASS'
                        )]) {
                            dir('services/market-data-service') {
                                sh "${WORKSPACE}/infra/jenkins/scripts/build_image.sh"
                            }
                        }
                    }
                }
                stage('portfolio-service') {
                    when { changeset "services/portfolio-service/**" }
                    steps {
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKERHUB_USER',
                            passwordVariable: 'DOCKERHUB_PASS'
                        )]) {
                            dir('services/portfolio-service') {
                                sh "${WORKSPACE}/infra/jenkins/scripts/build_image.sh"
                            }
                        }
                    }
                }
            }
        }
        stage('Push Images') {
            parallel {
                stage('instrument-service') {
                    when { 
                        branch 'main'
                        changeset "services/instrument-service/**" 
                    }
                    steps {
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKERHUB_USER',
                            passwordVariable: 'DOCKERHUB_PASS'
                        )]) {
                            dir('services/instrument-service') {
                                sh "${WORKSPACE}/infra/jenkins/scripts/push_image.sh"
                            }
                        }
                    }
                }
                stage('market-data-service') {
                    when { 
                        branch 'main'
                        changeset "services/market-data-service/**" 
                    }
                    steps {
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKERHUB_USER',
                            passwordVariable: 'DOCKERHUB_PASS'
                        )]) {
                            dir('services/market-data-service') {
                                sh "${WORKSPACE}/infra/jenkins/scripts/push_image.sh"
                            }
                        }
                    }
                }
                stage('portfolio-service') {
                    when { 
                        branch 'main'
                        changeset "services/portfolio-service/**" 
                    }
                    steps {
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKERHUB_USER',
                            passwordVariable: 'DOCKERHUB_PASS'
                        )]) {
                            dir('services/portfolio-service') {
                                sh "${WORKSPACE}/infra/jenkins/scripts/push_image.sh"
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        success {
            withCredentials([string(credentialsId: 'notification-email', variable: 'MAIL_TO')]){
                mail to: "${MAIL_TO}",
                subject: "Pipeline succeeded: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build ${env.BUILD_NUMBER} delivered successfully"
            }
        }
        failure {
            withCredentials([string(credentialsId: 'notification-email', variable: 'MAIL_TO')]){
                mail to: "${MAIL_TO}",
                subject: "Pipeline failure: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Check console output: ${env.BUILD_URL}"
            }
        }
        always {
            cleanWs()
        }
    }
}