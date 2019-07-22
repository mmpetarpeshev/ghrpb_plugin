def utils

pipeline {
  
  agent any

  parameters {
    string(name: 'BRANCH',
            defaultValue: 'master',
            description: 'Choose master to deploy to both staging and production env. Choose any other feature branch to deploy to staging only')
    choice(name: 'JENKINS_URL', choices: 'http://localhost:8080', description: 'Choose jenkins url  where the plugin should be installed ?')

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
          echo "Installing plugin ...."
          //String CRUMB_URL = """http://admin:1178ebd333f5adbab57b3e98f42a673177@${params.JENKINS_URL}/crumbIssuer/api/xml?xpath=concat\\(//crumbRequestField,":",//crumb\\)"""
          //echo "$CRUMB_URL"
          try {
            
            CRUMB = sh (script: """curl -s 'http://admin:d37b341d117b4e1c9968087a0931650b@${params.JENKINS_URL}/crumbIssuer/api/xml?xpath=concat\\(//crumbRequestField,":",//crumb\\)'""",returnStdout: true)      
            echo "$CRUMB"
            //sh """curl -X POST -H "'$CRUMB'" --user admin:d37b341d117b4e1c9968087a0931650b -i -F file=@ghprb.hpi ${params.JENKINS_URL}/pluginManager/uploadPlugin"""
          } catch (Exception e) {
             echo e.getMessage()
          }
        }
      }
    } 
  }
}


