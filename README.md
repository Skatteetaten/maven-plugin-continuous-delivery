# Aurora CD plugin

Maven plugin to suggest versionnumber for the next release. The version number will be exposed as maven-property "newVersion".

The name can be changed by setting:

    -Dcd.version.accesibleFromProperty=<property-navn> 

Can be used in other plugins, like release-plugin or versions-plugin

Example:

	mvn aurora-cd:suggest-version versions:set -DnewVersion=${suggestedVersion}

See https://github.com/skatteetaten/aurora-git-version for more information on how version is set.

## Properties

| Property | Default-value | Beskrivelse |
|----------|---------------|-------------|
| accesibleFromProperty | newVersion | Property where the suggest version is available.  |
| tagBaseName | v | Prefix for versjon-tags that is used to suggest versionnumber. All tags in the pattern vX.X.X will be used to suggest the next versionnumber.|
| inferReleaseVersionsForBranches | true | aurora-cd:suggest-version will suggest release-version for all branches listed in the property `branchesToInferReleaseVersionsForCsv`.|
| branchesToInferReleaseVersionsForCsv | master | Branches to suggest release-version from if `inferReleaseVersionsForBranches` is true. |
