/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.shell.command;

import java.util.Arrays;
import java.util.List;

import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.NoParamCompleter;

/**
 * @author Baptiste Mesta
 */
public class LoginCommand extends ShellCommand {

    @Override
    public boolean execute(final List<String> args, final ShellContext context) throws Exception {
        String type = args.get(0).toLowerCase();

        if (context.isLogged() || context.isLoggedOnPlatform()) {
            System.out.println("Already logged to the tenant!");
            return false;
        }
        switch (type) {
            case "tenant":
                context.login(args.get(1), args.get(2));
                break;
            case "platform":
                context.loginPlatform(args.get(1), args.get(2));
                break;
            default:
                throw new IllegalArgumentException("type should be tenant or platform");
        }
        return true;
    }

    @Override
    public void printHelp() {
        System.out.println("Usage: login <tenant or platform> <username> <password>");
    }

    @Override
    public boolean validate(final List<String> args) {
        return args.size() == 3 && (args.get(0).equals("tenant") || args.get(0).equals("platform"));
    }

    @Override
    public List<BonitaCompleter> getCompleters() {
        return Arrays.asList((BonitaCompleter) new NoParamCompleter("<username> <password>"));
    }

    @Override
    public String getName() {
        return "login";
    }

}
