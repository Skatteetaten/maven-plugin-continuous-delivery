package ske.maven.plugins.versionnumber

import spock.lang.Specification

class ReleaseVersionEvaluatorSpec extends Specification {
	def "Suggested versionnumber expands current versionnumber by one segment"() {
        given:
        def existingVersions = ["1.2.1", "1.2.2", "1.3.1", "1.3.2", "1.3.3"]
        when:
        def suggestedReleaseVersion = new ReleaseVersionEvaluator("1-SNAPSHOT").suggestNextReleaseVersionFrom(existingVersions);
        then:
        suggestedReleaseVersion.toString() == "1.4"
    }
    
	def "Suggested versionnumber is X.0 when there are none existing versionnumbers to take into consideration"() {
        given:
        def existingVersions = ["1.2", "1.2.2", "1.3.1"]
        when:
        def suggestedReleaseVersion = new ReleaseVersionEvaluator("2-SNAPSHOT").suggestNextReleaseVersionFrom(existingVersions);
        then:
        suggestedReleaseVersion.toString() == "2.0"
    }
    
	def "Suggested versionnumber is X.0.1 when the only existing version to evaluate is X.0"() {
        given:
        def existingVersions = ["1.0"]
        when:
        def suggestedReleaseVersion = new ReleaseVersionEvaluator("1.0-SNAPSHOT").suggestNextReleaseVersionFrom(existingVersions);
        then:
        suggestedReleaseVersion.toString() == "1.0.1"
    }
    
	def "versionnumbers not matching current verison is not taken into consideration"() {
        given:
        def existingVersions = ["1.1", "2.1", "3.1"]
        when:
        def suggestedReleaseVersion = new ReleaseVersionEvaluator("2-SNAPSHOT").suggestNextReleaseVersionFrom(existingVersions);
        then:
        suggestedReleaseVersion.toString() == "2.2"
    }
    
	def "SNAPSHOTS are excluded from evaluation"() {
        given:
        def existingVersions = ["1.2-SNAPSHOT", "1-SNAPSHOT", "1.3-SNAPSHOT", "2-SNAPSHOT"]
        when:
        def suggestedReleaseVersion = new ReleaseVersionEvaluator("1-SNAPSHOT").suggestNextReleaseVersionFrom(existingVersions);
        then:
        suggestedReleaseVersion.toString() == "1.0"
    }
    
	def "The list of existing versionnumbers can be unordered"() {
        given:
        def existingVersions = ["1.2", "1.3", "1.2.1", "1.5", "1.6", "1.4"]
        when:
        def suggestedReleaseVersion = new ReleaseVersionEvaluator("1-SNAPSHOT").suggestNextReleaseVersionFrom(existingVersions);
        then:
        suggestedReleaseVersion.toString() == "1.7"
    }
}
