pipeline {
    agent any

     environment {
        SONARQUBE_URL = "http://localhost:9001"
        SONARQUBE_TOKEN = "sqp_a6d48813f639cd7c65ebcd8b193676593420a51b"
    }

    triggers {
        // Run the pipeline when changes are pushed to the specified Git repository
        pollSCM('*/5 * * * *') // Poll every 5 minutes, adjust the schedule as needed
    }

    stages {
        stage('Review node and npm installations') {
            steps {
                nodejs(nodeJSInstallationName: 'node20') {
                sh 'npm -v'  //substitute with your code
                sh 'node -v'
                }
            }
        }
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