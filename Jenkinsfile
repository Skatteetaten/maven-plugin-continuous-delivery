#!groovy

node {
  stage('Checkout') {
    checkout scm
  }

  stage('Compile') {
     sh "./mvnw clean install -B"
     stash excludes: 'target/', includes: '**', name: 'source'
  }

  stage('Bump version') {
    def isMaster = env.BRANCH_NAME == "master"
    def branchShortName = env.BRANCH_NAME.split("/").last()

    // Set version in pom.xml
    echo "My branch is: ${env.BRANCH_NAME} "

    if (isMaster) {
      def suggestedVersion
      sh "./mvnw ske.aurora.maven.plugins.continuous-delivery:continuous-delivery:suggest-version versions:set -DgenerateBackupPoms=false"
    } else {
      sh "./mvnw versions:set -DnewVersion=${branchShortName}-SNAPSHOT -DgenerateBackupPoms=false -B"
    }
    // Read pom.xml
    pom = readMavenPom file: 'pom.xml'

    if (isMaster) {
      echo "Creating git-tag: v-${pom.version}"
      sh "git tag -a v-${pom.version} -m 'Release ${pom.version} on master'"
      sh "git push --follow-tags"
    }

    // Set build name
    currentBuild.displayName = "$pom.version (${currentBuild.number})"
  }
}

node {
  stage('Deploy to nexus') {
    unstash 'source'
    sh "./mvnw deploy -DskipTests"
  }
}
