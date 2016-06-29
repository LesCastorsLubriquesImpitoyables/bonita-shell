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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.HelpCommand;
import org.bonitasoft.shell.command.LoginCommand;
import org.bonitasoft.shell.command.LogoutCommand;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.reflect.ReflectCommandFactory;

/**
 * @author Baptiste Mesta
 */
public abstract class BonitaShellFactory implements ShellFactory {

    private ShellConfiguration shellConfiguration;

    @Override
    public Shell createShell() throws Exception {
        List<ShellCommand> commands = new ArrayList<>();
        commands.add(new LoginCommand(getShellContext()));
        commands.add(new LogoutCommand(getShellContext()));
        commands.addAll(new ReflectCommandFactory(getShellContext()).createTenantCommands(shellConfiguration.getTenantAPIClassNames()));
        commands.addAll(
                new ReflectCommandFactory(getShellContext()).createPlatformCommands(shellConfiguration.getPlatformAPIClassNames()));
        HashMap<String, ShellCommand> commandsMap = new HashMap<>();
        for (final ShellCommand shellCommand : commands) {
            commandsMap.put(shellCommand.getName(), shellCommand);
        }
        HelpCommand helpCommand = new HelpCommand(commandsMap);
        commandsMap.put(helpCommand.getName(), helpCommand);

        PrintColor.init();
        PrintColor.enable(shellConfiguration.isColorActivated());
        getShellContext().setConnectionParameters(shellConfiguration.getApiAccessType(), shellConfiguration.getConnectionParameters());
        Shell shell = new Shell();
        shell.setConfiguration(shellConfiguration);
        shell.setShellContext(getShellContext());
        shell.setCommands(commandsMap);
        shell.setHelpCommand(helpCommand);
        return shell;
    }

    public abstract ShellContext getShellContext();

    @Override
    public void setConfiguration(ShellConfiguration shellConfiguration) {
        this.shellConfiguration = shellConfiguration;
    }
}
