package ske.maven.plugins.versionnumber

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.operation.BranchListOp
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.eclipse.jgit.errors.RepositoryNotFoundException

@Mojo(name = "suggest-version", requiresDirectInvocation = true, requiresProject = true, aggregator = true, defaultPhase=LifecyclePhase.VALIDATE)
class SuggestNextVersionNumberMojo extends AbstractMojo {
    @Parameter(property = "cd.version.accesibleFromProperty", defaultValue = "cd.suggestedVersion")
    String accessibleFromProperty
    @Parameter(defaultValue = '${project.artifactId}', required = true)
    String tagBaseName
    @Parameter(defaultValue = '${project.version}', required = true, readonly = true)
    String currentVersion
    @Parameter(defaultValue = '${project}', readonly = true)
    private MavenProject project;
    
    void execute() {
        try {
            def currentVersionNumber = VersionNumber.parse(currentVersion)
            def git = getCurrentGitRepo()
            def tags = git.tag.list().findAll {it.name.startsWith(tagBaseName)}.collect {it.name - tagBaseName - ~/[^\d]*/}
            def suggestedVersion = new ReleaseVersionEvaluator(currentVersion).suggestNextReleaseVersionFrom(tags)
            project.getProperties().put(accessibleFromProperty, suggestedVersion.toString())
            getLog().info("Suggested version (${suggestedVersion}) accessible from \${${accessibleFromProperty}}")
        } catch (RepositoryNotFoundException x) {
            throw new MojoExecutionException("Could not open git-repository in current directory. Please make sure the project is contained in a git repository", x);
        }
    }
    
    Grgit getCurrentGitRepo() {
        return Grgit.open(dir: ".");
    }
}

