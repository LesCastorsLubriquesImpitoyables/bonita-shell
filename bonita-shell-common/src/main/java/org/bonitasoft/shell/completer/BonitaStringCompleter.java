package org.bonitasoft.shell.completer;

import java.util.List;

import jline.console.completer.StringsCompleter;

/**
 * Created by baptiste on 08/07/14.
 */
public class BonitaStringCompleter extends StringsCompleter implements BonitaCompleter {

    public BonitaStringCompleter(String... candidates) {
        super(candidates);
    }

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        String lastArgument = commandLine.getLastArgument();
        return complete(lastArgument, lastArgument.length(), candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return new CompletionHelper() {

            @Override
            public String getHelp(ArgumentParser argumentParser) {
                StringBuilder possibleArgs = new StringBuilder();
                for (String string : getStrings()) {
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

}
