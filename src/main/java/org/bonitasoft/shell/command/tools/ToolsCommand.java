package org.bonitasoft.shell.command.tools;

import java.util.Arrays;
import java.util.List;

import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.BonitaStringCompleter;
import org.bonitasoft.shell.completer.SubCommandStringCompleter;

public class ToolsCommand extends ShellCommand {

    public static final String COMMAND_NAME = "tools";

    private static final String[] SUB_COMMAND_NAME = { ReplayFailedTaskCommand.COMMAND_NAME, SearchLockedCommand.COMMAND_NAME };

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public void printHelp() {
        System.out.println("Usage: " + COMMAND_NAME + " <SUB_COMMAND_NAME> (e.g. " + SUB_COMMAND_NAME + ")");
    }

    @Override
    public boolean validate(List<String> args) {
        boolean valid = true;

        if (args.size() <= 0) {
            valid = false;
            //throw new IllegalArgumentException("Missing sub command");
        }

        if (Arrays.asList(SUB_COMMAND_NAME).contains(args.get(0))) {
            valid = true;
        }

        return valid;
    }

    @Override
    public boolean execute(List<String> args, ShellContext context) throws Exception {

        switch (args.get(0)) {
            case ReplayFailedTaskCommand.COMMAND_NAME:
                (new ReplayFailedTaskCommand()).execute(args.subList(1, args.size()), context);
                break;

            case SearchLockedCommand.COMMAND_NAME:
                (new SearchLockedCommand()).execute(args.subList(1, args.size()), context);
                break;

            default:
                throw new Exception("Unknown tools sub commande: " + args.get(0));
        }

        return false;
    }

    @Override
    public List<BonitaCompleter> getCompleters() {
        List<ShellCommand> subCommands = Arrays.asList(new ReplayFailedTaskCommand(), new SearchLockedCommand());

        return Arrays.asList(new BonitaStringCompleter(SUB_COMMAND_NAME), new SubCommandStringCompleter(subCommands));
    }
}
