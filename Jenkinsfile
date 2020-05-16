pipeline {
   agent any

   stages {
      stage('Verify Branch') {
         steps {
            echo "$GIT_BRANCH"
            sh(script: 'docker images -a')
            sh(script: 'cd /var/jenkins_home/workspace/watchlist-service')
            sh(script: 'docker build . watchlist-service')
            sh(script: 'docker images -a')
         }
      }
   }
}
