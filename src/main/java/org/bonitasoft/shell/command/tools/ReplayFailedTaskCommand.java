package org.bonitasoft.shell.command.tools;

import java.util.List;

import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.ShellCommand;

public class ReplayFailedTaskCommand extends ShellCommand {

    public static final String COMMAND_NAME = "replay_failed_task";

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean execute(List<String> args, ShellContext context) throws Exception {
        ProcessAPI processAPI = context.getProcessAPI();

        return false;
    }

    @Override
    public void printHelp() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean validate(List<String> args) {
        // TODO Auto-generated method stub
        return false;
    }

}
