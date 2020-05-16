pipeline {
   agent any

   stages {
      stage('Verify Branch') {
         steps {
            echo "$GIT_BRANCH"
            sh(script: 'docker images -a')
            sh(script: 'cd /var/jenkins_home/workspace/watchlist-service')
            sh(script: 'sbt assembly')
            sh(script: 'docker build -t watchlist-service .')
            sh(script: 'docker images -a')
         }
      }
   }
}
