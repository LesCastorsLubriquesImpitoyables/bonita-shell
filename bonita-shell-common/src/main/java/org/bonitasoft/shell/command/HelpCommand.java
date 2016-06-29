/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.completer.BonitaCompleter;

import jline.console.completer.StringsCompleter;

/**
 * Default implementation of the help command
 *
 * @author Baptiste Mesta
 */
public class HelpCommand extends ShellCommand {

    private final HashMap<String, ShellCommand> commands;

    @Override
    public String getDescription() {
        return "The Help";
    }

    /**
     * @param commands
     */
    public HelpCommand(final HashMap<String, ShellCommand> commands) {
        this.commands = commands;
    }

    @Override
    public boolean execute(final List<String> args, final ShellContext context) throws Exception {
        commands.get(args.get(0)).printHelp();
        return true;
    }

    @Override
    public void printHelp() {
        printUsage();
    }

    @Override
    public boolean validate(final List<String> args) {
        return args.size() == 1 && commands.containsKey(args.get(0));
    }

    @Override
    public List<BonitaCompleter> getCompleters() {
        return Collections.singletonList((BonitaCompleter) new StringsCompleter(commands.keySet()));
    }

    private void printUsage() {
        System.out.println("-----------------------------------------------");
        System.out.println("Usage: <Command> <arguments>");
        System.out.println("Command can be:");

        for (final ShellCommand command : commands.values()) {
            if (command.isActive()) {
                System.out.println(PrintColor.getGreen(command.getName()) + " -- " + command.getDescription());
            }
        }
        System.out.println("");
        System.out.println("Use 'help <Command>' for help about a command");
        System.out.println("-----------------------------------------------");
    }

    @Override
    public String getName() {
        return "help";
    }
}
