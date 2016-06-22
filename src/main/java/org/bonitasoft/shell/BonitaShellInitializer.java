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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.util.APITypeManager;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.LoginCommand;
import org.bonitasoft.shell.command.LogoutCommand;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.reflect.ReflectCommandFactory;

/**
 * @author Baptiste Mesta
 */
public class BonitaShellInitializer implements ShellInitializer {

    private Properties config;
    private List<ShellCommand> commands;

    public BonitaShellInitializer(Properties config) {
        this.config = config;
    }

    @Override
    public List<ShellCommand> getShellCommands() throws Exception {
        return commands;
    }

    @Override
    public void initialize() throws Exception {
        commands = new ArrayList<>();
        commands.add(new LoginCommand());
        commands.add(new LogoutCommand());
        commands.addAll(new ReflectCommandFactory().createTenantCommands(getCommaSeparatedValues("API.tenant.classes")));
        commands.addAll(new ReflectCommandFactory().createPlatformCommands(getCommaSeparatedValues("API.platform.classes")));
        initConnectionProperties();
        login();
        PrintColor.enable(Boolean.valueOf(config.getProperty("shell.color")));

    }

    private List<String> getCommaSeparatedValues(String key) {
        String apiClassesAsString = config.getProperty(key);
        String[] apiClasses = apiClassesAsString.replaceAll(" ", "").split(",");
        return Arrays.asList(apiClasses);
    }

    private void login() {
        ShellContext instance = ShellContext.getInstance();
        String username = config.getProperty("username");
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

    private void initConnectionProperties() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        for (final String name : config.stringPropertyNames()) {
            map.put(name, config.getProperty(name));
        }
        APITypeManager.setAPITypeAndParams(ApiAccessType.valueOf(config.getProperty("org.bonitasoft.engine.api-type")), map);
    }
}
