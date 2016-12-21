package ske.aurora.maven.plugins.versionnumber

class ReleaseVersionEvaluator {
  def currentVersion

  public ReleaseVersionEvaluator(String versionNumber) {
    this.currentVersion = VersionNumber.parse(versionNumber)
  }

  def suggestNextReleaseVersionFrom(listOfVersions) {
    def orderedListOfEligibleVersions = listOfVersions
        .findAll { VersionNumber.isValid(it) }
        .collect { VersionNumber.parse(it) }
        .sort()
        .findAll { currentVersion.canBeUsedWhenDeterminingReleaseVersion(it) }
    if (orderedListOfEligibleVersions.empty) {
      return currentVersion.unlockVersion()
    } else {
      orderedListOfEligibleVersions.last().adaptTo(currentVersion).incrementLastSegment()
    }
  }
}