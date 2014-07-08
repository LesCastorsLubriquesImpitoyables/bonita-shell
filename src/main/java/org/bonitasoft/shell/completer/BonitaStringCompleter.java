package org.bonitasoft.shell.completer;

import jline.console.completer.StringsCompleter;

import java.util.List;

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
        return complete(lastArgument,lastArgument.length(), candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }
}
