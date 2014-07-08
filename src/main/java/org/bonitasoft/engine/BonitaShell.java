package org.bonitasoft.engine;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.shell.BaseShell;
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.LoginCommand;
import org.bonitasoft.shell.command.LogoutCommand;
import org.bonitasoft.shell.command.ReflectCommand;
import org.bonitasoft.shell.command.ShellCommand;

public class BonitaShell extends BaseShell {

    public BonitaShell() {
    }

    public static void main(final String[] args) throws Exception {
        final BaseShell shell = new BonitaShell();
        shell.init();
        shell.run(System.in, System.out);
        shell.destroy();
    }

    @Override
    protected ShellContext getContext() {
        return BonitaShellContext.getInstance();
    }

    @Override
    protected List<ShellCommand> initShellCommands() throws Exception {
        ArrayList<ShellCommand> commands = new ArrayList<ShellCommand>(4);
        commands.add(new LoginCommand());
        commands.add(new LogoutCommand());
        commands.add(new ReflectCommand("process", ProcessAPI.class));
        commands.add(new ReflectCommand("identity", IdentityAPI.class));
        commands.add(new ReflectCommand("profile", ProfileAPI.class));
        return commands;
        // return Arrays.asList(createCommand(LoginCommand.class), createCommand(LogoutCommand.class), createCommand(DeployOrganisationCommand.class),
        // createCommand(CreateGroupCommand.class),
        // createCommand(CreateUserCommand.class), createCommand(CreateRoleCommand.class), createCommand(ListUserCommand.class),
        // createCommand(DeployBARCommand.class), createCommand(ListProcessDefinitions.class), createCommand(StartProcessCommand.class),
        // createCommand(EnableProcess.class), createCommand(AddUserToProcess.class));
    }

}
