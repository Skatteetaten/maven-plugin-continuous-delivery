package ske.aurora.maven.plugins.versionnumber

import org.ajoberstar.grgit.Grgit
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository

import ske.aurora.gitversion.GitVersion

@Mojo(name = "suggest-version", requiresDirectInvocation = true, requiresProject = true, aggregator = true, defaultPhase = LifecyclePhase.VALIDATE)
class SuggestNextVersionNumberMojo extends AbstractMojo {

  @Parameter(property = "accesibleFromProperty", defaultValue = "newVersion")
  String accessibleFromProperty

  @Parameter(defaultValue = 'v', required = true)
  String tagBaseName

  @Parameter(defaultValue = '${project.version}', required = true, readonly = true)
  String currentVersion

  @Parameter(defaultValue = 'master', required = true)
  String stipulateNextReleaseVersionForBranchesCsv

  @Parameter(defaultValue = '${project}', readonly = true)
  private MavenProject project;

  void execute() {
    String suggestedVersion = getSuggestedVersion()
    project.getProperties().put(accessibleFromProperty, suggestedVersion)
    getLog().info("Suggested version (${suggestedVersion}) accessible from \${${accessibleFromProperty}}")
  }

  private String getSuggestedVersion() {

    Grgit git = getCurrentGitRepo()

    def gitVersionOptions = new GitVersion.Options(versionPrefix: tagBaseName)
    GitVersion.Version versionFromGit = GitVersion.determineVersion(git.repository.rootDir, gitVersionOptions)

    Repository repository = git.repository.jgit.repository
    ObjectId head = repository.resolve("HEAD");
    String currentBranch = GitVersion.getBranchName(repository, head, true, 'BRANCH_NAME')
    List<String> stipulateNextReleaseVersionForBranches = stipulateNextReleaseVersionForBranchesCsv
        .split(',').
        collect { it.trim() }

    String suggestedVersion
    if (stipulateNextReleaseVersionForBranches.contains(currentBranch)) {
      suggestedVersion = versionFromGit.version
    } else {
      if (versionFromGit.source == GitVersion.VersionSource.TAG) {
        suggestedVersion = versionFromGit.version
      } else {
        def tags = git.tag.list()
            .findAll { it.name.startsWith(tagBaseName) }
            .collect { it.name - tagBaseName - ~/[^\d]*/ }

        VersionNumber stipulatedReleaseVersion = new ReleaseVersionEvaluator(currentVersion).
            suggestNextReleaseVersionFrom(tags)
        suggestedVersion = stipulatedReleaseVersion.toString()
      }
    }
    suggestedVersion
  }

  static Grgit getCurrentGitRepo() {
    try {
      return Grgit.open(dir: ".");
    } catch (RepositoryNotFoundException x) {
      throw new MojoExecutionException("Could not open git-repository in current directory. " +
          "Please make sure the project is contained in a git repository", x);
    }
  }
}

