Plugin for å foreslå hvilket versjonsnummer neste release skal ha. Versjonsnummeret blir eksponert som en maven-property "cd.suggestedVersion". Denne kan benyttes i andre plugins, f.eks. release-plugin eller versions-plugin.  
Eksempel:

	mvn ske-cd:suggest-version versions:set -DnewVersion=${cd.suggestedVersion}

Versjonsnummeret beregnes ut fra hvilken snapshotversjon man har og hvilke versjoner som er tagget i git, hvor "-SNAPSHOT" blir erstattet med et inkrement fra nyeste versjon som matcher snapshotversjonen.
Dersom pom'en angir versjon 1-SNAPSHOT og det ikke eksisterer noen tags i git vil foreslått versjonsnummer være 1.0. Om man har tagget versjonene 1.2, 1.4 og 1.8 vil forslaget for neste versjon være 1.9.
Om man har angitt versjon 3.14-SNAPSHOT i pom'en og man har versjonene 2.99, 3.14.9, 3.14.15, 3.20 og 4.0 vil foreslått versjonsnummer være 3.15.  
(se [ReleaseVersionEvaluatorSpec](src/test/groovy/ske/maven/plugins/findnextversionnumber/ReleaseVersionEvaluatorSpec.groovy) for flere eksempler)

[![Build Status](http://uil0folk-bygg-master01:8080/buildStatus/icon?job=continuous-delivery-maven-plugin&style=plastic)](http://uil0folk-bygg-master01:8080/job/continuous-delivery-maven-plugin)