#!/usr/bin/env groovy

node {
  stage('Checkout') {
    checkout scm
  }

  stage('Compile') {
     sh "./mvnw clean install -B"
  }

  stage('Bump version') {

    // Set version in pom.xml
    echo "My branch is: ${env.BRANCH_NAME} "

    origPom = readMavenPom file: 'pom.xml'
    sh "./mvnw ske.aurora.maven.plugins:aurora-cd:${origPom.version}:suggest-version versions:set -DgenerateBackupPoms=false"
    // Read pom.xml
    pom = readMavenPom file: 'pom.xml'

    // Set build name
    currentBuild.displayName = "$pom.version (${currentBuild.number})"
  }

  stage('Deploy to nexus') {
    sh "./mvnw deploy -DskipTests"
  }
}
