package org.bonitasoft.shell.completer;

import org.bonitasoft.shell.command.ReflectCommand;

import java.util.List;

/**
 * Created by baptiste on 08/07/14.
 */
public class ReflectMethodCompletionHelper implements CompletionHelper {

    private ReflectCommand command;

    public ReflectMethodCompletionHelper(ReflectCommand command) {
        this.command = command;
    }

    @Override
    public void addHelp(ArgumentParser argumentParser, List<CharSequence> candidates) {
        String methodHelp = command.getMethodHelp(argumentParser.getLastArgument());
        if (methodHelp != null) {
            candidates.add("**HELP"+methodHelp);
        }
    }
}
