/*
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.shell;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.HelpCommand;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.CommandArgumentsCompleter;
import org.bonitasoft.shell.completer.reflect.ReflectCandidateListCompletionHandler;

import jline.console.ConsoleReader;
import jline.console.history.FileHistory;

/**
 * A basic shell
 * Implement abstract methods
 * to run it just execute (e.g. in a main)
 * shell.run();
 *
 * @author Baptiste Mesta
 */
public class Shell {

    private static final String PROMPT = "bonita> ";

    private HashMap<String, ShellCommand> commands;

    private HelpCommand helpCommand;
    private ShellConfiguration shellConfiguration;
    private ShellContext shellContext;

    void setCommands(HashMap<String, ShellCommand> commands) {
        this.commands = commands;
    }

    void setHelpCommand(HelpCommand helpCommand) {
        this.helpCommand = helpCommand;
    }

    /**
     * called by {@link Shell} when the shell is exited
     *
     * @throws Exception
     */
    public void destroy() throws Exception {
        PrintColor.clean();
    }

    public void run(final InputStream in, final OutputStream out) throws Exception {
        printWelcomeMessage();
        final ConsoleReader reader = new ConsoleReader(in, out);
        reader.setBellEnabled(false);
        final CommandArgumentsCompleter commandArgumentsCompleter = new CommandArgumentsCompleter(commands);

        reader.setCompletionHandler(new ReflectCandidateListCompletionHandler());
        reader.addCompleter(commandArgumentsCompleter);
        reader.setHistory(new FileHistory(new File(".history")));

        String line;
        while ((line = reader.readLine("\n" + getPrompt())) != null) {
            final List<String> args = parse(line);
            final String command = args.remove(0);
            if (commands.containsKey(command)) {
                final ShellCommand clientCommand = commands.get(command);
                if (clientCommand.validate(args)) {
                    try {
                        clientCommand.execute(args, shellContext);
                    } catch (Throwable e) {

                        if (e instanceof InvocationTargetException) {
                            final InvocationTargetException invocationTargetException = (InvocationTargetException) e;
                            e = invocationTargetException.getTargetException();
                        }
                        if (e instanceof UndeclaredThrowableException) {
                            final UndeclaredThrowableException undeclaredThrowableException = (UndeclaredThrowableException) e;
                            e = undeclaredThrowableException.getUndeclaredThrowable();
                        }
                        PrintColor.printRedBold(e.getMessage());
                        if (shellConfiguration.isDebug()) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    clientCommand.printHelp();
                }
            } else if ("exit".equals(line)) {
                System.out.println("Exiting application");
                destroy();
                return;
            } else {
                System.out.println("Wrong argument");
                helpCommand.printHelp();
            }
        }
        destroy();
    }

    /**
     * used to parse arguments of the line
     *
     * @param line
     * @return
     */
    protected List<String> parse(final String line) {
        final List<String> asList = Arrays.asList(line.trim()
                .replaceAll("\\\\ ", "%SPACE%").split("(\\s)+"));
        for (int i = 0; i < asList.size(); i++) {
            final String string = asList.get(i);
            asList.set(i, string.replaceAll("%SPACE%", " "));
        }
        return new ArrayList<>(asList);
    }

    protected String getPrompt() {
        return PrintColor.getRedBold(shellContext.getLoggedUser()) + "@" + PrintColor.getBlue(PROMPT);
    }

    protected void printWelcomeMessage() {
        System.out.println("Welcome to Bonita Shell.\n For assistance press TAB or type \"help\" then hit ENTER.");
        PrintColor.printRedBold("     .ZZ         ");
        PrintColor.printRedBold("    .ZZ          ");
        PrintColor.printRedBold("    ZZ           ");
        PrintColor.printRedBold("   ZZ.     ZZZZZ ______             _ _        _____ _          _ _ ");
        PrintColor.printRedBold("  ZZZ     ZZ .   | ___ \\           (_) |      /  ___| |        | | |");
        PrintColor.printRedBold("  ZZ.     .ZZ    | |_/ / ___  _ __  _| |_ __ _\\ `--.| |__   ___| | |");
        PrintColor.printRedBold("  ZZ  ZZZ. ZZZ   | ___ \\/ _ \\| '_ \\| | __/ _` |`--. \\ '_ \\ / _ \\ | |");
        PrintColor.printRedBold("  ZZ. .    ZZZ   | |_/ / (_) | | | | | || (_| /\\__/ / | | |  __/ | |");
        PrintColor.printRedBold("  ZZZ.   .ZZZ.   \\____/ \\___/|_| |_|_|\\__\\__,_\\____/|_| |_|\\___|_|_|");
        PrintColor.printRedBold("  ~ZZZZZZZZZ.    ");
        PrintColor.printRedBold("    IZZZZZ.      ");
        PrintColor.printRedBold("");

        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
    }

    public void setConfiguration(ShellConfiguration shellConfiguration) throws Exception {

        this.shellConfiguration = shellConfiguration;
    }

    public void setShellContext(ShellContext shellContext) {
        this.shellContext = shellContext;
    }
}
