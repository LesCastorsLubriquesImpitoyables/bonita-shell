package org.bonitasoft.shell.completer.type;

import java.util.Arrays;
import java.util.List;

import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;
import org.bonitasoft.shell.completer.ResolvingStringsCompleter;

/**
 * Created by baptiste on 08/07/14.
 */
public class StringCompleter implements TypeHandler<String> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public String getValue(String argument) {
        return argument;
    }

    @Override
    public boolean isCastableTo(String argument) {
        return true;
    }

}
