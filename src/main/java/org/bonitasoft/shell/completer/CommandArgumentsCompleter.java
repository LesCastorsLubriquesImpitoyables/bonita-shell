/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell.completer;

import static jline.internal.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;

import jline.console.completer.Completer;
import org.bonitasoft.shell.command.ShellCommand;

/**
 * Allow to complete a set of given commands
 * Each command can have different completer
 * At the start of the line the completer complete with command name
 * Then it completes the line using command's completers
 *
 * @author Baptiste Mesta
 */
public class CommandArgumentsCompleter implements Completer {

    private final HashMap<String, ShellCommand> commands;

    private final APICompleter commandCompleter;

    /**
     * @param commands
     */
    public CommandArgumentsCompleter(final HashMap<String, ShellCommand> commands) {
        this.commands = commands;
        commandCompleter = new APICompleter(commands);
    }

    @Override
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        checkNotNull(candidates);
        final int pos = commandCompleter.complete(buffer, cursor, candidates);
        if (pos != -1) {
            return pos;
        }
        if (buffer != null) {
            final ArgumentParser argumentParser = new ArgumentParser(buffer);
            final String command = argumentParser.getCommand();
            if (command != null) {
                final int lastArgumentIndex = Math.max(argumentParser.getLastArgumentIndex(), 0);
                // complete with element from completer of the command
                final ShellCommand clientCommand = commands.get(command);
                if (clientCommand != null) {
                    final List<BonitaCompleter> completers = clientCommand.getCompleters();
                    if (completers.isEmpty()) {
                        return argumentParser.getOffset();
                    }
                    final BonitaCompleter completer = completers.get(Math.min(lastArgumentIndex, Math.max(0, completers.size() - 1)));
                    if (argumentParser.isLastArgumentCompleted()) {

                        CompletionHelper completionHelper = completer.getCompletionHelper();
                        if (completionHelper != null) {
                            String help = completionHelper.getHelp(argumentParser);
                            if (help != null) {
                                candidates.add("**HELP" + help);
                            }
                        }
                        return (argumentParser.getLastArgument() != null ? argumentParser.getLastArgument().length() : 0) + argumentParser.getOffset();
                    } else {
                        final int complete = completer.complete(argumentParser, candidates);
                        return complete + argumentParser.getOffset();
                    }
                }
            }
        }

        if (candidates.size() == 1) {
            candidates.set(0, candidates.get(0) + " ");
        }
        return candidates.isEmpty() ? -1 : cursor;
    }

}
