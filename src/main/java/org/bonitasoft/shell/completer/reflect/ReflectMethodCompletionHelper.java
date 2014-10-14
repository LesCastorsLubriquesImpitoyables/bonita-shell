package org.bonitasoft.shell.completer.reflect;

import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;

/**
 * Created by baptiste on 08/07/14.
 */
public class ReflectMethodCompletionHelper implements CompletionHelper {

    private ReflectCommand command;

    public ReflectMethodCompletionHelper(ReflectCommand command) {
        this.command = command;
    }

    @Override
    public String getHelp(ArgumentParser argumentParser) {
        return command.getMethodHelp(argumentParser.getLastArgument());
    }

}
