package ske.aurora.maven.plugins.versionnumber

class VersionNumber implements Comparable<VersionNumber> {
  public final static String not_snapshot
  public static final String snapshot_notation = "-SNAPSHOT"
  def versionNumberSegments;
  def isSnapshot;

  private VersionNumber(versionNumberSegments, isSnapshot = not_snapshot) {
    this.versionNumberSegments = versionNumberSegments
    this.isSnapshot = isSnapshot
  }

  public String toString() {
    return versionNumberSegments.join(".") + (isSnapshot ? snapshot_notation : "");
  }

  public static boolean isValid(String versionString) {
    if (versionString.contains("${snapshot_notation}")) {
      return versionString ==~ /\d+(.\d+)*(${snapshot_notation})?/
    } else {
      return versionString ==~ /^(\d+\.)(\d+\.)(\d+)$/
    }

  }

  public static VersionNumber parse(String versionString) {
    if (!isValid(versionString)) {
      throw new IllegalArgumentException("the version number $versionString is not well formatted")
    }
    return new VersionNumber((versionString - snapshot_notation).tokenize("."),
        versionString.endsWith(snapshot_notation))
  }

  public VersionNumber shorten(int newLength) {
    new VersionNumber(versionNumberSegments[0..newLength - 1], isSnapshot);
  }

  public int compareTo(VersionNumber other) {
    def transposedVersions = [this.versionNumberSegments, other.versionNumberSegments].transpose()
    def versionComparison = transposedVersions.collect { it[0] as int <=> it[1] as int };
    def versionAndSnapshotComparison = versionComparison + (this.isSnapshot <=> other.isSnapshot)
    def versionSnapshotAndLengthComparison = versionAndSnapshotComparison +
        (this.versionNumberSegments.size() <=> other.versionNumberSegments.size())
    return versionSnapshotAndLengthComparison.find { it != 0 }
  }

  public boolean canBeUsedWhenDeterminingReleaseVersion(VersionNumber other) {
    if (other.isSnapshot || !this.isSnapshot) {
      return false
    }
    if (other.versionNumberSegments.size() > this.versionNumberSegments.size()) {
      other = other.shorten(versionNumberSegments.size())
    }

    return this.versionNumberSegments == other.versionNumberSegments;
  }

  public VersionNumber unlockVersion() {
    def increaseSegment = versionNumberSegments.size() - 3
    if (Math.abs(increaseSegment) == 0) {
      return new VersionNumber(versionNumberSegments)
    } else {
      for (int i = 0; i < Math.abs(increaseSegment); i++) {
        versionNumberSegments += 0;
      }
      return new VersionNumber(versionNumberSegments)
    }
  }

  public VersionNumber adaptTo(VersionNumber example) {
    def newSize
    if (example.versionNumberSegments.size == 3) {
      newSize = versionNumberSegments.size()
    } else {
      newSize = versionNumberSegments.size() - (versionNumberSegments.size() - 3)
    }

    //def newSize = example.versionNumberSegments.size() + 1
    def adaption = versionNumberSegments.take(newSize);
    adaption += [0] * (newSize - adaption.size)
    return new VersionNumber(adaption)
  }

  public VersionNumber incrementLastSegment() {
    def segments = versionNumberSegments[0..-2] + ((versionNumberSegments.last() as int) + 1)
    return new VersionNumber(segments)
  }
}