pipeline {
    agent any

    tools {
        // Use NodeJS installation named "NodeJS_14" (adjust as needed)
        node "NodeJS_14"
    }

    environment {
        // Adjust these environment variables as needed
        SONARQUBE_URL = "http://your-sonarqube-server:9000"
        SONARQUBE_TOKEN = credentials('sonar-token-id')
    }

    triggers {
        // Run the pipeline when changes are pushed to the specified Git repository
        pollSCM('*/5 * * * *') // Poll every 5 minutes, adjust the schedule as needed
    }

    stages {
        stage('Install Dependencies') {
            steps {
                script {
                    // Install project dependencies
                    sh 'npm install'
                }
            }
        }

        stage('Run Cypress Tests') {
            steps {
                script {
                    // Run Cypress tests
                    sh 'npx cypress run'
                }
            }
        }

        stage('Run Istanbul for Code Coverage') {
            steps {
                script {
                    // Run Istanbul for code coverage
                    sh 'npx nyc --reporter=lcov --reporter=text-summary npm run test'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    // Run SonarQube analysis
                    withSonarQubeEnv('SonarQube') {
                        sh "npm run sonar-scanner -Dsonar.host.url=${env.SONARQUBE_URL} -Dsonar.login=${env.SONARQUBE_TOKEN}"
                    }
                }
            }
        }
    }
}