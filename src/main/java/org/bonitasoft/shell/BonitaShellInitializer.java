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

import com.bonitasoft.engine.api.ReportingAPI;
import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.util.APITypeManager;
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
        commands = new ArrayList<ShellCommand>();
        commands.add(new LoginCommand());
        commands.add(new LogoutCommand());
        commands.addAll(new ReflectCommandFactory().createCommands(Arrays.asList(ProcessAPI.class.getName(),
                IdentityAPI.class.getName(), ReportingAPI.class.getName())));
        initHome();
        initiLib();
        login();

    }

    private void initiLib() {
        Scanner s = new Scanner(System.in);
        String next = s.next();

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

    protected void initHome() throws IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        for (final String name : config.stringPropertyNames()) {
            map.put(name, config.getProperty(name));
        }
        APITypeManager.setAPITypeAndParams(ApiAccessType.valueOf(config.getProperty("org.bonitasoft.engine.api-type")), map);
        /*
         * File homeFoler = null;
         * if (System.getProperty("bonita.home") == null) {
         * homeFoler = new File("home");
         * FileUtils.deleteDirectory(homeFoler);
         * homeFoler.mkdir();
         * File file = new File(homeFoler, "client");
         * file.mkdir();
         * file = new File(file, "conf");
         * file.mkdir();
         * file = new File(file, "bonita-client.properties");
         * file.createNewFile();
         * final Properties properties = new Properties();
         * properties.load(this.getClass().getResourceAsStream(
         * "/bonita-client.properties"));
         * final String application = System.getProperty("shell.application");
         * if (application != null) {
         * properties.put("application.name", application);
         * }
         * final String host = System.getProperty("shell.host");
         * final String port = System.getProperty("shell.port");
         * properties.put("server.url", "http://"
         * + (host != null ? host : "localhost") + ":"
         * + (port != null ? port : "8080"));
         * final FileWriter writer = new FileWriter(file);
         * try {
         * properties.store(writer, "Server configuration");
         * } finally {
         * writer.close();
         * }
         * System.out.println("Using server configuration " + properties);
         * System.setProperty("bonita.home", homeFoler.getAbsolutePath());
         * }
         */
    }
}
