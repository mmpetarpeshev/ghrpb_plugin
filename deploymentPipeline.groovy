def utils



pipeline {
  agent { label 'deployment' }

  parameters {
    string(name: 'BRANCH',
            defaultValue: 'master',
            description: 'Choose master to deploy to both staging and production env. Choose any other feature branch to deploy to staging only')
    choice(name: 'JENKINS_URL', choices: 'https://jenkins-staging.leanplum.com', description: 'Choose jenkins url  where the plugin should be installed ?')

  }

  options {
    disableConcurrentBuilds()
    ansiColor('xterm')
  }

  stages {
    
  stage("Build ghrp plugin from leanplum fork repository...") {
      steps {
        script {
            sh 'mvn package' 
        }
      }
    }

    stage ("Install the plugin to selected jenkins instance...") {
      steps {
        script {
          echo "Plugin is installed!"
        }
      }
    } 
  }

}


