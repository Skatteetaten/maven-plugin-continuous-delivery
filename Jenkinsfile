#!/usr/bin/env groovy

def jenkinsfile
fileLoader.withGit('https://git.sits.no/git/scm/ao/aurora-pipeline-scripts.git', 'feature/AOS-711-generere-semantisk-riktige-versjoner') {
   jenkinsfile = fileLoader.load('templates/bibliotek')
}

jenkinsfile.run('feature/AOS-711-generere-semantisk-riktige-versjoner', 'Maven 3', 'ci_aos', 'ci_aos')