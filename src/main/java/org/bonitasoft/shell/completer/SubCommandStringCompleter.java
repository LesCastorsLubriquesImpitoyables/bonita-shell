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
        ShellCommand command = searchCommandStartingWith(commandLine);
        if (command != null) {
            return searchSubCommandCompleterToUse(commandLine, command).complete(commandLine, candidates);
        } else {
            return 0;
        }
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return new CompletionHelper() {

            @Override
            public String getHelp(ArgumentParser argumentParser) {
                ShellCommand command = searchCommandStartingWith(argumentParser);
                return searchSubCommandCompleterToUse(argumentParser, command).getCompletionHelper().getHelp(argumentParser);
            }
        };
    }

    ShellCommand searchCommandStartingWith(ArgumentParser commandLine) {
        // Get sub command name (firsts chars) typed by user
        String firstArgument = commandLine.getFirstArgument();

        // Iterate over all sub command
        for (ShellCommand shellCommand : subCommands) {
            // Search sub command match user typing
            if (shellCommand.getName().startsWith(firstArgument)) {
                // Call command completer that match the argument number
                return shellCommand;
            }
        }

        return null;
    }

    static BonitaCompleter searchSubCommandCompleterToUse(ArgumentParser commandLine, ShellCommand command) {
        return command.getCompleters().get(commandLine.getLastArgumentIndex() - 1);
    }

}
