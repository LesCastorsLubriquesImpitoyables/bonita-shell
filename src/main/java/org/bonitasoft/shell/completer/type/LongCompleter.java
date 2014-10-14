package org.bonitasoft.shell.completer.type;

import java.util.List;

import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;

/**
 * Created by baptiste on 08/07/14.
 */
public class LongCompleter  implements TypeHandler<Long> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public Long getValue(String argument) {
            return Long.valueOf(argument);
    }

    @Override
    public boolean isCastableTo(String argument) {
        try{
            Long.valueOf(argument);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getString(Long result) {
        return String.valueOf(result);
    }
}
