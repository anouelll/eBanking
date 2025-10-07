pipeline {
    agent any
    
    environment {
        // Define environment variables
        PROJECT_NAME = 'MyProject'
        BUILD_DIR = 'build'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout code from version control
                git url: 'https://github.com/myorg/myrepo.git'  
            }
        }
        stage('Build Backend') {
            steps {
                script {
                    // Build backend using Maven
                    sh 'mvn clean install -DskipTests'
                }
            }
        }
        stage('Test Backend') {
            steps {
                script {
                    // Run backend tests
                    sh 'mvn test'
                }
            }
        }
        stage('Build Frontend') {
            steps {
                script {
                    // Navigate to frontend directory and build using npm
                    dir('frontend') {
                        sh 'npm install'
                        sh 'npm run build'
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image
                    sh "docker build -t ${PROJECT_NAME}:latest ."
                }
            }
        }
        stage('Push Docker Image') {
            steps { 
                script {
                    // Push Docker image to registry
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                        sh "docker tag ${PROJECT_NAME}:latest mydockerhubuser/${PROJECT_NAME}:latest"
                        sh "docker push mydockerhubuser/${PROJECT_NAME}:latest"
                    }
                }
            }
        }
        