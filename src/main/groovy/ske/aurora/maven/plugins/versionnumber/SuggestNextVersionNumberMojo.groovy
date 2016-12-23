package ske.aurora.maven.plugins.versionnumber

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

@Mojo(name = "suggest-version", requiresDirectInvocation = true, requiresProject = true, aggregator = true, defaultPhase = LifecyclePhase.VALIDATE)
class SuggestNextVersionNumberMojo extends AbstractMojo {

  @Parameter(property = "accesibleFromProperty", defaultValue = "newVersion")
  String accessibleFromProperty

  @Parameter(defaultValue = 'v', required = true)
  String tagBaseName

  @Parameter(defaultValue = '${project.version}', required = true, readonly = true)
  String currentVersion

  @Parameter(defaultValue = 'master', required = true)
  String branchesToStipulateReleaseVersionsForCsv

  @Parameter(defaultValue = '${project}', readonly = true)
  private MavenProject project;

  void execute() {

    List<String> branchesToStipulateReleaseVersionsFor = branchesToStipulateReleaseVersionsForCsv
        .split(',').
        collect { it.trim() }

    def options = new VersionNumberSuggester.Options(
        versionPrefix: tagBaseName,
        branchesToInferReleaseVersionsFor: branchesToStipulateReleaseVersionsFor,
        versionHint: currentVersion
    )

    String suggestedVersion = VersionNumberSuggester.suggestVersion(options)

    project.getProperties().put(accessibleFromProperty, suggestedVersion)
    getLog().info("Suggested version (${suggestedVersion}) accessible from \${${accessibleFromProperty}}")
  }

}

