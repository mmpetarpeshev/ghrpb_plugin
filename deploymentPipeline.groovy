def utils

pipeline {
  
  agent any

  parameters {
    string(name: 'BRANCH',
            defaultValue: 'master',
            description: 'Choose master to deploy to both staging and production env. Choose any other feature branch to deploy to staging only')
    choice(name: 'JENKINS_URL', choices: 'localhost:8080', description: 'Choose jenkins url  where the plugin should be installed ?')

  }

  environment {
        MAVEN_HOME = tool('mvn')
    }

  options {
    disableConcurrentBuilds()
    ansiColor('xterm')
  }

  stages {
    
  stage("Build ghrp plugin from leanplum fork repository...") {
      steps {
        script {
            sh '${MAVEN_HOME}/bin/mvn package' 
        }
      }
    }

    stage ("Install the plugin to selected jenkins instance...") {
      steps {
        script {
          dir("target") {
            echo "Installing plugin ...."
            withCredentials([usernameColonPassword(credentialsId: 'jenkinsAdmin', variable: 'USERPASS')]) { 
            def CRUMB = sh (script: """curl -s 'http://$USERPASS@${params.JENKINS_URL}/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)'""",returnStdout: true)

            sh """curl -X POST -H "$CRUMB" --user $USERPASS -i -F file=@ghprb.hpi http://${params.JENKINS_URL}/pluginManager/uploadPlugin"""
          }
        }
       }
      }
    }
  }
} 



