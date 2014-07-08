package org.bonitasoft.shell.completer;

import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.ReflectCommand;

/**
 * Created by baptiste on 08/07/14.
 */
public class ReflectMethodCompletionHelper implements CompletionHelper {

    private ReflectCommand command;

    public ReflectMethodCompletionHelper(ReflectCommand command) {
        this.command = command;
    }

    @Override
    public void printHelp(String argument) {
        String methodHelp = command.getMethodHelp(argument);
        if (methodHelp != null) {

            PrintColor.printGreenBold("\n"+methodHelp);
        }
    }
}
