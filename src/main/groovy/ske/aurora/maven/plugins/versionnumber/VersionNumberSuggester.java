package ske.aurora.maven.plugins.versionnumber;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import ske.aurora.gitversion.GitTools;
import ske.aurora.gitversion.GitVersion;

public class VersionNumberSuggester {

    public static String suggestVersion() throws IOException {
        return suggestVersion(new Options());
    }

    public static String suggestVersion(Options options) throws IOException {

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File("./", ".git"))
            .readEnvironment()
            .setMustExist(true)
            .build();

        GitVersion.Options gitVersionOptions = new GitVersion.Options();
        gitVersionOptions.setVersionPrefix(options.versionPrefix);

        GitVersion.Version versionFromGit = GitVersion.determineVersion(repository, gitVersionOptions);

        Optional<String> currentBranchOption = GitTools.getBranchName(repository, true, "BRANCH_NAME");

        String currentBranch = currentBranchOption
            .orElseThrow(() -> new IllegalStateException("Unable to determine name of current branch"));

        return determineVersion(repository, options, versionFromGit, currentBranch);
    }

    private static String determineVersion(Repository repository, Options options,
        GitVersion.Version versionFromGit, String currentBranch) {

        if (!options.branchesToStipulateReleaseVersionsFor.contains(currentBranch)) {
            return versionFromGit.getVersion();
        }

        if (versionFromGit.getSource() == GitVersion.VersionSource.TAG) {
            return versionFromGit.getVersion();
        }

        List<String> versions = getAllVersionsFromTags(repository, options.versionPrefix);

        VersionNumber stipulatedReleaseVersion = new ReleaseVersionEvaluator(options.versionHint)
            .suggestNextReleaseVersionFrom(versions);
        return stipulatedReleaseVersion.toString();
    }

    private static List<String> getAllVersionsFromTags(Repository repository, String versionPrefix) {

        return repository.getTags().entrySet().stream()
            .filter(e -> e.getKey().startsWith(versionPrefix))
            .map(e -> e.getKey().replaceFirst(versionPrefix, ""))
            .collect(Collectors.toList());
    }

    public static class Options {

        List<String> branchesToStipulateReleaseVersionsFor = Collections.singletonList("master");

        String versionPrefix = "v";

        String versionHint = null;

        public List<String> getBranchesToStipulateReleaseVersionsFor() {
            return branchesToStipulateReleaseVersionsFor;
        }

        public void setBranchesToStipulateReleaseVersionsFor(List<String> branchesToStipulateReleaseVersionsFor) {
            this.branchesToStipulateReleaseVersionsFor = branchesToStipulateReleaseVersionsFor;
        }

        public String getVersionPrefix() {
            return versionPrefix;
        }

        public void setVersionPrefix(String versionPrefix) {
            this.versionPrefix = versionPrefix;
        }

        public String getVersionHint() {
            return versionHint;
        }

        public void setVersionHint(String versionHint) {
            this.versionHint = versionHint;
        }
    }
}
