package ske.aurora.maven.plugins.versionnumber

import spock.lang.Specification

class VersionNumberSpec extends Specification {

  def "version number with major, minor and revision versions outputs X.Y.Z"() {
    given:
      def version = "1.2.3"
    when:
      def versionNumber = VersionNumber.parse(version)
    then:
      versionNumber.toString() == "1.2.3"
  }

  def "version number with two adjacent periods is on wrong format"() {
    given:
      def version = "1..2"
    when:
      def versionNumber = VersionNumber.parse(version)
    then:
      IllegalArgumentException exception = thrown();
      exception.message == "the version number 1..2 is not well formatted"
  }

  def "Version number elements support multiple digits"() {
    given:
      def version = "1234567.2345678.2322"
    when:
      def versionNumber = VersionNumber.parse(version)
    then:
      versionNumber.toString() == "1234567.2345678.2322"
  }

  def "Version number support snapshots"() {
    given:
      def version = "1.2-SNAPSHOT"
    when:
      def versionNumber = VersionNumber.parse(version)
    then:
      versionNumber.isSnapshot == true
      versionNumber.toString() == "1.2-SNAPSHOT"
  }

  def "Version number without snapshot-notation is not marked as snapshot"() {
    given:
      def version = "1.2.0"
    when:
      def versionNumber = VersionNumber.parse(version)
    then:
      versionNumber.isSnapshot == false
  }

  def "Version numbers are naturally sorted by their individual segments"() {
    given:
      def unsortedVersions = [
          VersionNumber.parse("8.2-SNAPSHOT"),
          VersionNumber.parse("8.2.8"),
          VersionNumber.parse("1.8.9"),
          VersionNumber.parse("8.2.0"),
          VersionNumber.parse("10.0.1")].asImmutable()
    when:
      def sortedVersions = unsortedVersions.toSorted()
    then:
      sortedVersions.collect { it.toString() } == ["1.8.9", "8.2.0", "8.2.8", "8.2-SNAPSHOT", "10.0.1"]
  }

  def "Snapshot versions are considered greater than equal release"() {
    given:
      def baseVersion = VersionNumber.parse("3.3.0");
      def snapshotVersion = VersionNumber.parse("3.3.0-SNAPSHOT");
    when:
      def difference = baseVersion.compareTo(snapshotVersion);
    then:
      difference == -1
  }

  def "Snapshot versions are considered greater than same version with more digits"() {
    given:
      def baseVersion = VersionNumber.parse("3.3.1");
      def snapshotVersion = VersionNumber.parse("3.3-SNAPSHOT");
    when:
      def difference = baseVersion.compareTo(snapshotVersion);
    then:
      difference == -1
  }

  def "A shortened snapshot is still a snapshot"() {
    given:
      def originalVersion = VersionNumber.parse("3.3.1-SNAPSHOT");
    when:
      def shortenedVerison = originalVersion.shorten(2);
    then:
      shortenedVerison.toString() == "3.3-SNAPSHOT"
  }

  def "A version number with the same leading version numbers can be used when determining release version"() {
    given:
      def releasedVersion = VersionNumber.parse("3.3.1");
      def developmentVersion = VersionNumber.parse("3.3-SNAPSHOT");
    when:
      def similar = developmentVersion.canBeUsedWhenDeterminingReleaseVersion(releasedVersion);
    then:
      similar == true
  }

  def "Shorter version numbers autopads length when adapting to longer version number"() {
    given:
      def releasedVersion = VersionNumber.parse("3.4.0");
      def developmentVersion = VersionNumber.parse("3.4-SNAPSHOT");
    when:
      def adaptedVersion = releasedVersion.adaptTo(developmentVersion);
    then:
      adaptedVersion.toString() == "3.4.0";
  }

  def "last version number is incremented"() {
    given:
      def version = VersionNumber.parse("3.2.1");
    when:
      def increasedVersion = version.incrementLastSegment();
    then:
      increasedVersion.toString() == "3.2.2";
  }

  def "Version numbers is extended with another segment with value 0 when unlocked"() {
    given:
      def developmentVersion = VersionNumber.parse("3.3-SNAPSHOT");
    when:
      def unlockedVersion = developmentVersion.unlockVersion();
    then:
      unlockedVersion.toString() == "3.3.0";
  }

  def "example of valid version numbers"() {
    given:
      def versions = [
          "1.0.1",
          "2.3.4-SNAPSHOT",
          "1.1.2",
          "1.1.1",
          "123456789.123456789.123456789",
          "123456789.123456789.123456789",
          "18.19.20",
          "1.2-SNAPSHOT"
      ]
    when:
      def versionNumbers = versions.collect {
        try {
          VersionNumber.parse(it);
        } catch (IllegalArgumentException x) {
          println x.message; throw x
        }
      }
    then:
      notThrown IllegalArgumentException
  }

  def "example of invalid version numbers"() {
    given:
      def versions = [
          ".1",
          "1.2",
          ".123456789",
          "1..1",
          "123456789..123456789",
          "1..1..1",
          "123456789..123456789..123456789",
          "1..1.1..1",
          "123456789..123456789.123456789..123456789",
          "12.0.0.1"
      ]
    when:
      def versionNumbers = versions.collect {
        try {
          VersionNumber.parse(it);
        } catch (IllegalArgumentException) {
          return null
        }
      }
    then:
      println versionNumbers.findAll { it }.join()
      versionNumbers.findAll { it }.size() == 0
  }

}

