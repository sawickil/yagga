package org.yu55.yagga.handler.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yu55.yagga.handler.git.command.common.GitCommandExecutorFactory;

@Component
public class GitRepositories {

    private static final Logger logger = LoggerFactory.getLogger(GitRepositories.class);

    private final List<GitRepository> repositories;

    private GitCommandExecutorFactory gitCommandExecutorFactory;

    @Autowired
    public GitRepositories(@Value("${repositories.paths}") String[] pathsToRepositories,
                    GitCommandExecutorFactory gitCommandExecutorFactory) {
        this.gitCommandExecutorFactory = gitCommandExecutorFactory;
        repositories = new LinkedList<>();
        initDirectories(Arrays.asList(pathsToRepositories));

    }

    public List<GitRepository> getRepositories() {
        return repositories;
    }

    public Optional<GitRepository> getRepositoryByDirectoryName(String name) {
        return repositories
                .stream()
                .filter(repo -> repo.isDirectoryNameEqual(name))
                .findFirst();
    }

    private void initDirectories(List<String> pathsToRepositories) {
        for (String ptr : pathsToRepositories) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(new File(ptr).toPath())) {
                for (Path entry : stream) {
                    if (isGitRepository(entry)) {
                        repositories.add(new GitRepository(entry.toFile(), gitCommandExecutorFactory));
                    }
                }
            } catch (IOException ex) {
                logger.error("Cannot obtain repositories directories", ex);
            }
        }
    }

    private boolean isGitRepository(Path filePath) {
        File file = filePath.toFile();
        return file.isDirectory() && new File(file, ".git").exists();
    }
}
