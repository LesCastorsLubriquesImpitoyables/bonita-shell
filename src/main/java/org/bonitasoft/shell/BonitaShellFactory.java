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
import java.util.Scanner;

import org.bonitasoft.engine.util.APITypeManager;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.HelpCommand;
import org.bonitasoft.shell.command.LoginCommand;
import org.bonitasoft.shell.command.LogoutCommand;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.reflect.ReflectCommandFactory;

/**
 * @author Baptiste Mesta
 */
public class BonitaShellFactory implements ShellFactory {

    private List<ShellCommand> commands;
    private ShellConfiguration shellConfiguration;

    private void login() {
        ShellContext instance = ShellContext.getInstance();
        String username = shellConfiguration.getDefaultUsername();
        if (username != null) {
            System.out.println("Enter password for " + username);
            Scanner s = new Scanner(System.in);
            String next = s.next();
            try {
                instance.login(username, next);
            } catch (Exception e) {
                System.out.println("Wrong credential, login manually");
            }
        }
    }

    @Override
    public Shell createShell() throws Exception {
        commands = new ArrayList<>();
        commands.add(new LoginCommand());
        commands.add(new LogoutCommand());
        commands.addAll(new ReflectCommandFactory().createTenantCommands(shellConfiguration.getTenantAPIClassNames()));
        commands.addAll(new ReflectCommandFactory().createPlatformCommands(shellConfiguration.getPlatformAPIClassNames()));
        HashMap<String, ShellCommand> commandsMap = new HashMap<>();
        for (final ShellCommand shellCommand : commands) {
            commandsMap.put(shellCommand.getName(), shellCommand);
        }
        HelpCommand helpCommand = new HelpCommand(commandsMap);
        commandsMap.put(helpCommand.getName(), helpCommand);

        PrintColor.init();
        PrintColor.enable(shellConfiguration.isColorActivated());
        APITypeManager.setAPITypeAndParams(shellConfiguration.getApiAccessType(), shellConfiguration.getConnectionParameters());
        Shell shell = new Shell();
        shell.setConfiguration(shellConfiguration);
        shell.setCommands(commandsMap);
        shell.setHelpCommand(helpCommand);
        login();

        return shell;
    }

    @Override
    public void setConfiguration(ShellConfiguration shellConfiguration) {
        this.shellConfiguration = shellConfiguration;
    }
}
