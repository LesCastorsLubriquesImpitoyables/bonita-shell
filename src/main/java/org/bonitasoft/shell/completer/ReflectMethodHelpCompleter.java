/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell.completer;

import java.util.List;

import org.bonitasoft.shell.command.ReflectCommand;

/**
 * @author Baptiste Mesta
 */
public class ReflectMethodHelpCompleter implements BonitaCompleter {

    private final ReflectCommand reflectCommand;

    /**
     * @param reflectCommand
     */
    public ReflectMethodHelpCompleter(final ReflectCommand reflectCommand) {
        this.reflectCommand = reflectCommand;
    }


    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        String methodHelp = reflectCommand.getMethodHelp(commandLine.getLastArgument());
        if (methodHelp != null) {
            candidates.add("**HELP" + methodHelp);
        }
        return 0;
    }
}
