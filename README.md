# Aurora CD plugin

Plugin for å foreslå hvilket versjonsnummer neste release skal ha. Versjonsnummeret blir eksponert som maven-property "newVersion". 

Navnet kan endres ved å sette:

    -Dcd.version.accesibleFromProperty=<property-navn> 

Denne kan benyttes i andre plugins, f.eks. release-plugin eller versions-plugin.

Eksempel:

	mvn aurora-cd:suggest-version versions:set -DnewVersion=${suggestedVersion}

Se https://aurora/git/projects/AUF/repos/aurora-git-version/browse for mer informasjon om hvordan versjonen blir satt.

## Properties

| Property | Default-verdi | Beskrivelse |
|----------|---------------|-------------|
| accesibleFromProperty | newVersion | Property hvor den foreslåtte versjonen blir gjort tilgjengelig |
| tagBaseName | v | Prefix for versjons-tager som brukes til å foreslå versjonsnummer. Alle tags på formen vX.X.X blir brukt for å foreslå neste versjonsnummer.|
| inferReleaseVersionsForBranches | true | aurora-cd:suggest-version vil foreslå release-versjoner for alle branches listet i propertyen `branchesToInferReleaseVersionsForCsv`.|
| branchesToInferReleaseVersionsForCsv | master | Branches som det skal foreslås release-versjoner for dersom `inferReleaseVersionsForBranches` er satt til true. |
