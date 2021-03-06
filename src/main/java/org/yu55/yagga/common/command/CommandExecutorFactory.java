package org.yu55.yagga.common.command;

import java.io.File;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Component;
import org.yu55.yagga.common.repository.Repository;

@Component
public class CommandExecutorFactory {

    public CommandExecutor factorizeGlobalCommandExecutor(Command command) {
        return factorize(new File("."), null, command);
    }

    public CommandExecutor factorizeRepositoryCommandExecutor(Repository repository, Command command) {
        return factorize(repository.getDirectory().toFile(), repository.getDirectoryName(), command);
    }

    public CommandExecutor factorizeDirectoryCommandExecutor(File dir, Command command) {
        return factorize(dir, null, command);
    }

    private CommandExecutor factorize(File dir, String name, Command command) {
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(dir);
        CommandExecutorStreamHandler executorStreamHandler = new CommandExecutorStreamHandler(name);
        executor.setStreamHandler(new PumpStreamHandler(executorStreamHandler));
        return new CommandExecutor(command, executor, executorStreamHandler::getOutput);
    }

}
