#!/usr/bin/env groovy

def git
def maven
def utilitiesl
def scriptVersion='v3.0.0'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', scriptVersion) {
    git = fileLoader.load('git/git')
    maven = fileLoader.load('maven/maven')
    utilities = fileLoader.load('utilities/utilities')
}

Map<String, Object> props = [:]
deployProperties = "-P sign,build-extras"
props.put('deployProperties', deployProperties)
props.put('mavenSettingsFile', 'github-maven-settings')
props.put('pomPath', 'pom.xml')
props.put('credentialsId', 'github_bjartek')

node {
    stage('Checkout and Preparation') {
        maven.setMavenVersion('Maven 3')
        checkout scm
    }

    stage('Compile') {
        maven.install(props)
    }

    stage('Bump version') {
        origPom = readMavenPom file: 'pom.xml'
        maven.run("no.skatteetaten.aurora.maven.plugins:aurora-cd:${origPom.version}:suggest-version versions:set -DgenerateBackupPoms=false", props)
        // Read pom.xml
        pom = readMavenPom file: 'pom.xml'

        // Set build name
        currentBuild.displayName = "$pom.version (${currentBuild.number})"

        if (!pom.version.endsWith("-SNAPSHOT")) {
            git.tagIfNotExists(props.credentialsId, "v$pom.version")
        }
    }

    stage('Deploy to Maven Central') {
        if (!maven.existInMavenCentral(props.pomPath)) {
            maven.deploy(props, props.deployProperties)
        } else {
            echo "[INFO] Already exist in Maven Central, skip deploy"
        }
    }
}
