package ske.aurora.maven.plugins.versionnumber

import spock.lang.Specification

class ReleaseVersionEvaluatorSpec extends Specification {
  def "Suggested version number expands current version number by one segment"() {
    given:
      def existingVersions = ["1.2.1", "1.2.2", "1.3.1", "1.3.2", "1.3.3"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("1-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "1.3.4"
  }

  def "Suggested version number is X.0.0 when there are none existing version numbers to take into consideration"() {
    given:
      def existingVersions = ["1.2.1", "1.2.2", "1.3.1"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("2.0.0-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "2.0.0"
  }

  def "Suggested version number is X.X.0 when there are none existing version numbers to take into consideration"() {
    given:
      def existingVersions = ["1.2.1", "1.2.2", "1.3.1"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("2.3-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "2.3.0"
  }

  def "Suggested version number is X.0.1 when the only existing version to evaluate is X.0"() {
    given:
      def existingVersions = ["1.0.0"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("1.0-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "1.0.1"
  }

  def "version numbers not matching current version is not taken into consideration"() {
    given:
      def existingVersions = ["1.1.0", "2.1.0", "2.1.1", "2.2.0", "3.1.0"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("2-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "2.2.1"
  }

  def "SNAPSHOTS are excluded from evaluation"() {
    given:
      def existingVersions = ["1.2-SNAPSHOT", "1-SNAPSHOT", "1.3-SNAPSHOT", "2-SNAPSHOT"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("1-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "1.0.0"
  }

  def "The list of existing version numbers can be unordered"() {
    given:
      def existingVersions = ["1.2.0", "1.3.0", "1.2.1", "1.5.6", "1.6.2", "1.4.5"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("1-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "1.6.3"
  }
}
