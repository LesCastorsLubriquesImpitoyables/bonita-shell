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
package org.bonitasoft.shell.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An utility class to parse arguments for the {@link CommandArgumentsCompleter}
 *
 * @author Baptiste Mesta
 */
public class ArgumentParser {

    private final String original;

    private boolean completed = false;

    private List<String> arguments;

    private String command;

    private String firstArgument;

    private String lastArgument;

    private int offset;

    private String previousArgument;

    /**
     *
     */
    public ArgumentParser(final String string) {
        original = string;
        final List<String> list = new ArrayList<>(Arrays.asList(string.split("(\\s)+")));
        if (!original.isEmpty() && " ".equals(original.substring(original.length() - 1, original.length()))) {
            completed = true;
        }
        if (list.size() > 0) {
            command = list.get(0);
            if (list.size() > 1) {
                arguments = list.subList(1, list.size());
            } else {
                arguments = new ArrayList<>();
            }
        } else {
            arguments = new ArrayList<>();
        }
        if (arguments.size() > 0) {
            firstArgument = arguments.get(0);
            lastArgument = arguments.get(arguments.size() - 1);
            if (arguments.size() > 1) {
                previousArgument = arguments.get(arguments.size() - 2);
            }
            offset = original.lastIndexOf(lastArgument);
        } else {
            offset = original.length();
        }
    }

    public String getCommand() {
        return command;
    }

    public String getLastArgument() {
        return lastArgument;
    }

    public String getFirstArgument() {
        return firstArgument;
    }

    public int getLastArgumentIndex() {
        return arguments.size() - 1;
    }

    public boolean isLastArgumentCompleted() {
        return completed;
    }

    public int getOffset() {
        return offset;
    }

    public String getPreviousArgument() {
        return previousArgument;
    }

    public String getArgumentAt(int index) {
        return arguments.get(index);
    }
}
