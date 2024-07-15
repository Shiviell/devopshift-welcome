pipeline {
    agent any

    parameters {
       string( gitrepo = "https://github.com/Shiviell/devopshift-welcome.git" ) 
        //dockerCredentials = 'your-docker-credentials-id'  
       string( imageName = "productpage")  // Replace with your Docker image name
        string (BUILD_NUMBER="${env.BUILD_NUMBER}")
        string(branch = "jenkins-workshop")
        string(dockerfile = "devopshift-welcome/welcome/app/bookinfo/src/productpage" ) 
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
                    //sh "cd ${dockerfile}"
                   
                    sh "docker build -f ${dockerfile}/Dockerfile -t shivi2021/${imageName}:1.0.${BUILD_NUMBER} ${dockerfile}"
                    }
                }
            }
        

        stage('Test') {
            steps {
                script {
                // Run tests inside the Docker container
                sh "docker run -it -d -p 9080:9080 --name productpage shivi2021/${imageName}:1.0.${BUILD_NUMBER}" // docker run -it -d -p 9080:9080 --name productpage shivi2021/productpage:1.0.5
                sh "docker ps"
                sh "docker stop productpage"
                sh "docker rm productpage"
                }
            }
        }

        stage('Push') {
            steps {
                script {
                    // Push Docker image to registry
                    withCredentials([usernamePassword(credentialsId: 'dockerHub', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
                    sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPassword}"
                    sh "docker push shivi2021/${imageName}:1.0.${BUILD_NUMBER}"
                    }
                }
            }
        }
    }
}
