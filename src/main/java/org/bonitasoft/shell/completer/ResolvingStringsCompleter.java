/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell.completer;

import java.util.List;

import jline.console.completer.StringsCompleter;

/**
 * @author Baptiste Mesta
 */
public abstract class ResolvingStringsCompleter extends StringsCompleter implements BonitaCompleter {

    @Override
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        getStrings().clear();
        final List<String> resolveStrings = resolveStrings();
        if (resolveStrings != null) {
            getStrings().addAll(resolveStrings);
        }
        return super.complete(buffer, cursor, candidates);
    }

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        String lastArgument = commandLine.getLastArgument();
        return complete(lastArgument, lastArgument != null ? lastArgument.length() : 0, candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return new CompletionHelper() {

            @Override
            public String getHelp(ArgumentParser argumentParser) {
                StringBuilder possibleArgs = new StringBuilder();
                for (String string : resolveStrings()) {
                    possibleArgs.append(string).append(", ");
                }
                if (possibleArgs.length() > 2) {
                    return possibleArgs.substring(0, possibleArgs.length() - 2);
                } else {
                    return "";
                }
            }
        };
    }

    /**
     * @return
     */
    public abstract List<String> resolveStrings();
}
