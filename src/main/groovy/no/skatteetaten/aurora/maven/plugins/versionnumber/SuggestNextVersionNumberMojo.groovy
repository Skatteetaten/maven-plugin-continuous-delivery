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

  @Parameter(defaultValue = "master", readonly = false)
  String branchesToUseTagsAsVersionsForCsv

  @Parameter(defaultValue = "", required = false)
  String branchPrefixesForForcedPatchSegmentUpdate

  @Parameter(defaultValue = "", required = false)
  String branchPrefixesForForcedMinorSegmentUpdate

  @Parameter(defaultValue = '${project}', readonly = true)
  private MavenProject project

  def determineVersionNumberBasedOnBranchPrefix =
      !branchPrefixesForForcedPatchSegmentUpdate.isEmpty() || !branchPrefixesForForcedPatchSegmentUpdate.isEmpty()

  void execute() {

    List<String> branchesToInferReleaseVersionsFor = []
    if (inferReleaseVersionsForBranches) {
      branchesToInferReleaseVersionsFor = branchesToInferReleaseVersionsForCsv.split(',').collect { it.trim() }
    }
    List<String> branchesToUseTagsAsVersionsFor = branchesToUseTagsAsVersionsForCsv.split(',').collect { it.trim() }

    def options = new SuggesterOptions(
        versionPrefix: tagBaseName,
        branchesToInferReleaseVersionsFor: branchesToInferReleaseVersionsFor,
        versionHint: currentVersion,
        branchesToUseTagsAsVersionsFor: branchesToUseTagsAsVersionsFor,
        patchUpdateBranchPrefix: branchPrefixesForForcedPatchSegmentUpdate,
        minorUpdateBranchPrefix: branchPrefixesForForcedMinorSegmentUpdate,
        determineVersionNumberBasedOnBranchPrefix: determineVersionNumberBasedOnBranchPrefix
    )

    String suggestedVersion = VersionNumberSuggester.suggestVersion(options)

    project.getProperties().put(accessibleFromProperty, suggestedVersion)
    getLog().info("Suggested version (${suggestedVersion}) accessible from \${${accessibleFromProperty}}")
  }
}

