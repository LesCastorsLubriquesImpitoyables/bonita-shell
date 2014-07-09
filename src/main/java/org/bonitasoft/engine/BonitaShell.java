package org.bonitasoft.engine;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.shell.BaseShell;
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.LoginCommand;
import org.bonitasoft.shell.command.LogoutCommand;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.command.tools.ToolsCommand;
import org.bonitasoft.shell.completer.reflect.ReflectCommand;

import com.bonitasoft.engine.api.MonitoringAPI;
import com.bonitasoft.engine.api.PageAPI;
import com.bonitasoft.engine.api.ReportingAPI;
import com.bonitasoft.engine.api.ThemeAPI;

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
        return ShellContext.getInstance();
    }

    @Override
    protected List<ShellCommand> initShellCommands() throws Exception {
        final ArrayList<ShellCommand> commands = new ArrayList<>(4);
        commands.add(new LoginCommand());
        commands.add(new LogoutCommand());
        commands.add(new ReflectCommand(ProcessAPI.class));
        commands.add(new ReflectCommand(IdentityAPI.class));
        commands.add(new ReflectCommand(ProfileAPI.class));
        commands.add(new ReflectCommand(PageAPI.class));
        commands.add(new ReflectCommand(MonitoringAPI.class));
        commands.add(new ReflectCommand(ThemeAPI.class));
        commands.add(new ReflectCommand(ReportingAPI.class));
        commands.add(new ReflectCommand(CommandAPI.class));
        commands.add(new ToolsCommand());
        return commands;
        // return Arrays.asList(createCommand(LoginCommand.class), createCommand(LogoutCommand.class), createCommand(DeployOrganisationCommand.class),
        // createCommand(CreateGroupCommand.class),
        // createCommand(CreateUserCommand.class), createCommand(CreateRoleCommand.class), createCommand(ListUserCommand.class),
        // createCommand(DeployBARCommand.class), createCommand(ListProcessDefinitions.class), createCommand(StartProcessCommand.class),
        // createCommand(EnableProcess.class), createCommand(AddUserToProcess.class));
    }

}
