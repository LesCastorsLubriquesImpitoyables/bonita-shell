/*
 *
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
 *
 */
package org.bonitasoft.shell;

import java.io.IOException;
import java.util.List;

import org.bonitasoft.shell.command.ShellCommand;

/**
 *
 * Init the shell
 * It detects configuration ans if there is none ask to download drivers, bonita version
 * @author Baptiste Mesta
 */
public interface ShellInitializer {


    /**
     * @return list of commands contributed to the shell
     * @throws Exception
     */
    List<ShellCommand> getShellCommands() throws Exception;


    void initialize() throws IOException;
}
