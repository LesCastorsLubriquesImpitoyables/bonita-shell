/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import jline.console.ConsoleReader;

import org.apache.commons.io.FileUtils;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.HelpCommand;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.CommandArgumentsCompleter;
import org.bonitasoft.shell.completer.reflect.ReflectCandidateListCompletionHandler;

/**
 * A basic shell
 * Implement abstract methods
 * to run it just execute (e.g. in a main)
 * shell.run();
 *
 * @author Baptiste Mesta
 */
public abstract class BaseShell {

    private static final String PROMPT = "bonita> ";

    static final String HELP = "help";

    private HashMap<String, ShellCommand> commands;

    private HelpCommand helpCommand;

    private File homeFoler;

    public void init() throws Exception {
        final List<ShellCommand> commandList = initShellCommands();
        commands = new HashMap<String, ShellCommand>();
        for (final ShellCommand shellCommand : commandList) {
            commands.put(shellCommand.getName(), shellCommand);
        }
        helpCommand = getHelpCommand();
        if (helpCommand != null) {
            commands.put(helpCommand.getName(), helpCommand);
        }
        PrintColor.init();
        initHome();

    }

    /**
     * return the help command used
     * Can be overridden
     */
    protected HelpCommand getHelpCommand() {
        return new HelpCommand(commands);
    }

    /**
     * @return list of commands contributed to the shell
     * @throws Exception
     */
    protected abstract List<ShellCommand> initShellCommands() throws Exception;

    /**
     * called by {@link BaseShell} when the shell is exited
     *
     * @throws Exception
     */
    public void destroy() throws Exception {
        cleanHome();
        PrintColor.clean();
    }

    public void run(final InputStream in, final OutputStream out) throws Exception {
        init();
        printWelcomeMessage();
        final ConsoleReader reader = new ConsoleReader(in, out);
        reader.setBellEnabled(false);
        final CommandArgumentsCompleter commandArgumentsCompleter = new CommandArgumentsCompleter(commands);

        reader.setCompletionHandler(new ReflectCandidateListCompletionHandler());
        reader.addCompleter(commandArgumentsCompleter);

        String line;
        while ((line = reader.readLine("\n" + getPrompt())) != null) {
            final List<String> args = parse(line);
            final String command = args.remove(0);
            if (commands.containsKey(command)) {
                final ShellCommand clientCommand = commands.get(command);
                if (clientCommand.validate(args)) {
                    try {
                        clientCommand.execute(args, getContext());
                    } catch (final Exception e) {
                        e.printStackTrace();
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
     * @return
     */
    protected abstract ShellContext getContext();

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
        return new ArrayList<String>(asList);
    }

    protected void initHome() throws IOException {
        homeFoler = null;
        if (System.getProperty("bonita.home") == null) {
            homeFoler = new File("home");
            FileUtils.deleteDirectory(homeFoler);
            homeFoler.mkdir();
            File engineClient = new File(homeFoler, "engine-client");
            engineClient.mkdir();
            File file = new File(engineClient, "work");
            file.mkdir();
            file = new File(file, "bonita-client-community.properties");
            file.createNewFile();
            File file2 = new File(engineClient, "conf");
            file2.mkdir();
            file2 = new File(file2, "bonita-client-custom.properties");
            file2.createNewFile();
            final Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream(
                    "/bonita-client.properties"));
            final String application = System.getProperty("shell.application");
            if (application != null) {
                properties.put("application.name", application);
            }
            final String host = System.getProperty("shell.host");
            final String port = System.getProperty("shell.port");
            properties.put("server.url", "http://"
                    + (host != null ? host : "localhost") + ":"
                    + (port != null ? port : "8080"));

            final FileWriter writer = new FileWriter(file);
            final FileWriter writer2 = new FileWriter(file2);
            try {
                properties.store(writer, "Server configuration");
                properties.store(writer2, "Server configuration");
            } finally {
                writer.close();
                writer2.close();
            }
            System.out.println("Using server configuration " + properties);
            System.setProperty("bonita.home", homeFoler.getAbsolutePath());
        }
    }

    protected void cleanHome() throws IOException {
        if (homeFoler != null) {
            FileUtils.deleteDirectory(homeFoler);
        }
    }

    protected String getPrompt() {
        return PrintColor.getRedBold(getContext().getLoggedUser()) + "@" + PrintColor.getBlue(PROMPT);
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
}
