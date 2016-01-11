package org.bonitasoft.shell.completer.type;

import java.io.File;
import java.util.List;

import jline.console.completer.FileNameCompleter;

import org.bonitasoft.engine.job.FailedJob;
import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.CompletionHelper;

/**
 * Created by baptiste on 08/07/14.
 */
public class FailedJobTypeCompleter extends FileNameCompleter implements BonitaCompleter, TypeHandler<FailedJob> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return complete(commandLine.getLastArgument(), 0, candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public FailedJob getValue(String argument) {
        return null;
    }

    @Override
    public boolean isCastableTo(String argument) {
        return new File(argument).isFile();
    }

    @Override
    public String getString(FailedJob result) {
        return "Failed job -- jobDescriptorId " + result.getJobDescriptorId() + " name " + result.getJobName() + " description " + result.getDescription()
                + "\n" +
                "error is:\n" + result.getLastMessage();
    }
}
