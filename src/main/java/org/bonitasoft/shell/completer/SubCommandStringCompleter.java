package org.bonitasoft.shell.completer;

import java.util.List;

import org.bonitasoft.shell.command.ShellCommand;

public class SubCommandStringCompleter implements BonitaCompleter {

    private List<ShellCommand> subCommands;

    public SubCommandStringCompleter(List<ShellCommand> subCommands) {
        this.subCommands = subCommands;
    }

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        // Get sub command name (firsts chars) typed by user
        String firstArgument = commandLine.getFirstArgument();

        System.out.println("firstArgName: " + firstArgument);

        // Iterate over all sub command
        for (ShellCommand shellCommand : subCommands) {
            // Search sub command match user typing
            if (shellCommand.getName().startsWith(firstArgument)) {
                // Call command completer that match the argument number
                return shellCommand.getCompleters().get(commandLine.getLastArgumentIndex() - 1).complete(commandLine, candidates);
            }
        }

        return 0;
    }

}
