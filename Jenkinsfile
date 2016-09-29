#!/usr/bin/env groovy

node {
  stage('Checkout') {
    checkout scm
  }

  stage('Compile') {
     sh "./mvnw clean install -B"
  }

  stage('Bump version') {
    def isMaster = env.BRANCH_NAME == "master"
    def branchShortName = env.BRANCH_NAME.split("/").last()

    // Set version in pom.xml
    echo "My branch is: ${env.BRANCH_NAME} "

    if (isMaster) {
      // Read pom.xml
      origPom = readMavenPom file: 'pom.xml'
      sh "./mvnw ske.aurora.maven.plugins:aurora-cd:${origPom.version}:suggest-version versions:set -DgenerateBackupPoms=false"
    } else {
      sh "./mvnw versions:set -DnewVersion=${branchShortName}-SNAPSHOT -DgenerateBackupPoms=false -B"
    }
    // Read pom.xml
    pom = readMavenPom file: 'pom.xml'

    if (isMaster) {
      echo "Creating git-tag: v${pom.version}"
      sh "./mvnw scm:tag -Dusername=ci_aos -Dpassword=ci_aos -B"
    }

    // Set build name
    currentBuild.displayName = "$pom.version (${currentBuild.number})"
    stash excludes: 'target/', includes: '**', name: 'source'
  }
}

node {
  stage('Deploy to nexus') {
    unstash 'source'
    sh "./mvnw deploy -DskipTests"
  }
}
