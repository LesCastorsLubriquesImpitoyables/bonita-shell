package org.bonitasoft.shell.completer.type;

import java.util.List;

import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;

/**
 * Created by baptiste on 08/07/14.
 */
public class IntegerCompleter implements TypeHandler<Integer> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public Integer getValue(String argument) {
            return Integer.valueOf(argument);
    }

    @Override
    public boolean isCastableTo(String argument) {
        try{
            Integer.valueOf(argument);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getString(Integer result) {
        return String.valueOf(result);
    }
}
