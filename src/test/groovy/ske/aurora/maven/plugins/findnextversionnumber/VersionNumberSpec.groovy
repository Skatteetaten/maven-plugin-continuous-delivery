package ske.aurora.maven.plugins.versionnumber

import spock.lang.Specification

class VersionNumberSpec extends Specification {
	def "versionnumber with major and minor versions only outputs X.Y"() {
        given:
        def version = "1.2"
        when:
        def versionNumber = VersionNumber.parse(version)
        then:
        versionNumber.toString() == "1.2"
    }

    def "versionnumber with major, minor and revision versions outputs X.Y.Z"() {
        given:
        def version = "1.2.3"
        when:
        def versionNumber = VersionNumber.parse(version)
        then:
        versionNumber.toString() == "1.2.3"
    }

    def "versionnumber with two adjacent periods is on wrong format"() {
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
        def version = "1234567.2345678"
        when:
        def versionNumber = VersionNumber.parse(version)
        then:
        versionNumber.toString() == "1234567.2345678"
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
        def version = "1.2"
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
            VersionNumber.parse("8.2"),
            VersionNumber.parse("10.0.1")].asImmutable()
        when:
        def sortedVersions = unsortedVersions.toSorted()
        then:
        sortedVersions.collect{it.toString()} == ["1.8.9", "8.2", "8.2.8", "8.2-SNAPSHOT", "10.0.1"]
    }
    
    def "Version number with more digits after an equal number is considered greater"() {
        given:
        def baseVersion = VersionNumber.parse("3.3");
        def moreSpecificVersion = VersionNumber.parse("3.3.0");
        when:
        def difference = baseVersion.compareTo(moreSpecificVersion);
        then:
        difference == -1
    }
    
    def "Snapshot versions are considered greater than equal release"() {
        given:
        def baseVersion = VersionNumber.parse("3.3");
        def snapshotVersion = VersionNumber.parse("3.3-SNAPSHOT");
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
    
    def "A versionnumber can be shortened to a given amount of segments"() {
        given:
        def originalVersion = VersionNumber.parse("3.3.1.4.5");
        when:
        def shortenedVerison = originalVersion.shorten(2);
        then:
        shortenedVerison.toString() == "3.3"
        originalVersion != shortenedVerison
    }
    
    def "A shortened versionnumber is a copy of the original"() {
        given:
        def originalVersion = VersionNumber.parse("3.3.1.4.5");
        when:
        def shortenedVerison = originalVersion.shorten(2);
        then:
        originalVersion != shortenedVerison
    }
    
    def "A shortened snapshot is still a snapshot"() {
        given:
        def originalVersion = VersionNumber.parse("3.3.1-SNAPSHOT");
        when:
        def shortenedVerison = originalVersion.shorten(2);
        then:
        shortenedVerison.toString() == "3.3-SNAPSHOT"
    }
    
    def "A versionnumber with the same leading versionnumbers can be used when determining release version"() {
        given:
        def releasedVersion = VersionNumber.parse("3.3.1");
        def developmentVersion = VersionNumber.parse("3.3-SNAPSHOT");
        when:
        def similar = developmentVersion.canBeUsedWhenDeterminingReleaseVersion(releasedVersion);
        then:
        similar == true
    }
    
    def "Versionnumbers is shortened when adapting to a shorter versionnumber"() {
        given:
        def releasedVersion = VersionNumber.parse("4.3.2");
        def developmentVersion = VersionNumber.parse("4-SNAPSHOT");
        when:
        def adaptedVersion = releasedVersion.adaptTo(developmentVersion);
        then:
        adaptedVersion.toString() == "4.3";
    }
    
    def "Shorter versionnumbers autopads length when adapting to longer versionnumber"() {
        given:
        def releasedVersion = VersionNumber.parse("3.4");
        def developmentVersion = VersionNumber.parse("3.4-SNAPSHOT");
        when:
        def adaptedVersion = releasedVersion.adaptTo(developmentVersion);
        then:
        adaptedVersion.toString() == "3.4.0";
    }
    
    def "last versionNumber is incremented"() {
        given:
        def version = VersionNumber.parse("3.2.1");
        when:
        def increasedVersion = version.incrementLastSegment();
        then:
        increasedVersion.toString() == "3.2.2";
    }
    
    def "Versionnumbers is extended with another segment with value 0 when unlocked"() {
        given:
        def developmentVersion = VersionNumber.parse("3.3-SNAPSHOT");
        when:
        def unlockedVersion = developmentVersion.unlockVersion();
        then:
        unlockedVersion.toString() == "3.3.0";
    }
    
    def "example of valid versionnumbers"() {
        given:
        def versions = [
            "1",
            "123456789",
            "1.1",
            "123456789.123456789",
            "1.1.1",
            "123456789.123456789.123456789",
            "1.1.1.1",
            "123456789.123456789.123456789.123456789",
            "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20"
        ]
        when:
        def versionNumbers = versions.collect {try {VersionNumber.parse(it);} catch(IllegalArgumentException x){ println x.message; throw x}}
        then:
        notThrown IllegalArgumentException
    }
    
    def "example of invalid versionnumbers"() {
        given:
        def versions = [ 
            ".1", 
            ".123456789",
            "1..1",
            "123456789..123456789",
            "1..1..1",
            "123456789..123456789..123456789",
            "1..1.1..1",
            "123456789..123456789.123456789..123456789"]
        when:
        def versionNumbers = versions.collect{ try {VersionNumber.parse(it);} catch(IllegalArgumentException){ return null }}
        then:
        println versionNumbers.findAll {it}.join()
        versionNumbers.findAll {it}.size() == 0
    }
    
}

