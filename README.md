Plugin for å foreslå hvilket versjonsnummer neste release skal ha. Versjonsnummeret blir eksponert som maven-property "newVersion". 

Navnet kan endres ved å sette:

    -Dcd.version.accesibleFromProperty=<property-navn> 

Denne kan benyttes i andre plugins, f.eks. release-plugin eller versions-plugin.

Eksempel:

	mvn aurora-cd:suggest-version versions:set -DnewVersion=${suggestedVersion}

Se https://aurora/git/projects/AUF/repos/aurora-git-version/browse for mer informasjon om hvordan versjonen blir satt.