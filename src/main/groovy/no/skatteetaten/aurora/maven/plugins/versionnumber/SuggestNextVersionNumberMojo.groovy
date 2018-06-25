package no.skatteetaten.aurora.maven.plugins.versionnumber

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

import no.skatteetaten.aurora.version.SuggesterOptions
import no.skatteetaten.aurora.version.VersionNumberSuggester

@Mojo(name = "suggest-version", requiresDirectInvocation = true, requiresProject = true, aggregator = true, defaultPhase = LifecyclePhase.VALIDATE)
class SuggestNextVersionNumberMojo extends AbstractMojo {

  @Parameter(property = "accesibleFromProperty", defaultValue = "newVersion")
  String accessibleFromProperty

  @Parameter(defaultValue = 'v', required = true)
  String tagBaseName

  @Parameter(defaultValue = '${project.version}', required = true, readonly = true)
  String currentVersion

  @Parameter(defaultValue = "true", required = false)
  Boolean inferReleaseVersionsForBranches

  @Parameter(defaultValue = 'master', required = true)
  String branchesToInferReleaseVersionsForCsv

  @Parameter(defaultValue = "", required = false)
  String branchesToUseTagsAsVersionsForCsv

  @Parameter(property = "forcePatchIncrementForBranchPrefixes", defaultValue = "", required = false)
  String forcePatchIncrementForBranchPrefixes

  @Parameter(property = "forceMinorIncrementForBranchPrefixes", defaultValue = "", required = false)
  String forceMinorIncrementForBranchPrefixes

  @Parameter(defaultValue = '${project}', readonly = true)
  private MavenProject project

  void execute() {

    def branchesToInferReleaseVersionsFor = inferReleaseVersionsForBranches ?
        commaSeparatedStringToList(branchesToInferReleaseVersionsForCsv) : []

    def options = new SuggesterOptions(
        versionPrefix: tagBaseName,
        branchesToInferReleaseVersionsFor: branchesToInferReleaseVersionsFor,
        versionHint: currentVersion,
        branchesToUseTagsAsVersionsFor: commaSeparatedStringToList(branchesToUseTagsAsVersionsForCsv),
        forcePatchIncrementForBranchPrefixes: commaSeparatedStringToList(forcePatchIncrementForBranchPrefixes),
        forceMinorIncrementForBranchPrefixes: commaSeparatedStringToList(forceMinorIncrementForBranchPrefixes)
    )

    getLog().info("Using SuggesterOptions ${options.properties.toString()}")

    String suggestedVersion = VersionNumberSuggester.suggestVersion(options)

    project.getProperties().put(accessibleFromProperty, suggestedVersion)
    getLog().info("Suggested version (${suggestedVersion}) accessible from \${${accessibleFromProperty}}")
  }

  private static List<String> commaSeparatedStringToList(String commaSeparatedString) {
    if (commaSeparatedString?.trim()) {
      commaSeparatedString
          .split(",")
          .collect { it.trim() }
          .findAll { !it.isEmpty() }
    } else {
      []
    }
  }
}

