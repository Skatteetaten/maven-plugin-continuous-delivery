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
| branchPrefixesForForcedPatchSegmentUpdate | | This is by default turned off. See description in 'Update version number based on originating branch name'. |
| branchPrefixesForForcedMinorSegmentUpdate | | This is by default turned off. See description in 'Update version number based on originating branch name'. |

###Update version number based on originating branch name
Enabled by specifying one or both of the parameters 'branchPrefixesForForcedPatchSegmentUpdate' and 'branchPrefixesForForcedMinorSegmentUpdate'. Both supports a comma separated list of branch prefixes.
If the originating branch name of last merge starts with one of the given prefixes, the requested segment of the version number will be incremented, regardless of the version hint found from the current version in the POM. 
This to enable automatic semantic versioning based on the branch name prefix of last delivery.
<p>Example:
Current version is defined in the POM as `1-SNAPSHOT`, and last released version is `v1.2.4`. 
A hotfix is delivered from branch `hotfix/PROJ-123-some-reported-bug`. 
Executing this plugin without updating the POM will by default result in the suggested new version `v1.3.0`. 
By specifying `branchPrefixesForForcedPatchSegmentUpdate=bugfix,hotfix` 
the plugins behaviour is altered to force an update of the PATCH segment of the version number, 
since the branch prefix matches `hotfix`, resulting in the suggested new version `v.1.2.5`.
   