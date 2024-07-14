pipeline {
    agent any

    parameters {
       string( gitrepo = "https://github.com/Shiviell/devopshift-welcome.git" ) // Replace with your Docker registry URL
        //dockerCredentials = 'your-docker-credentials-id'  // Replace with your Jenkins Docker credentials ID
       string( imageName = "productpage")  // Replace with your Docker image name
        string (BUILD_NUMBER="${env.BUILD_NUMBER}")
        string(branch = "jenkins-workshop")
        string(dockerfile = "devopshift-welcome/welcome/app/bookinfo/src/productpage" ) // Assuming your Dockerfile is in the root of the repository
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout code from Git repository
                sh "git clone -b ${branch} ${gitrepo}"
            }
        }

        stage('Build') {
            steps {
                script {
                    // Build Docker image
                    sh "pwd"
                    sh "cd ${dockerfile}"
                   
                    sh "docker build --target build --tag shivi2021/${imageName}:1.0.${BUILD_NUMBER} ${dockerfile} "
                    }
                }
            }
        

        stage('Test') {
            steps {
                // Run tests inside the Docker container
                sh "docker run shivi2021/${imageName}:1.0.${BUILD_NUMBER}"
            }
        }

        stage('Push') {
            steps {
                script {
                    // Push Docker image to registry
                    docker.withRegistry("${dockerRegistry}", dockerCredentials) {
                        customImage.push()
                    }
                }
            }
        }
    }
}
