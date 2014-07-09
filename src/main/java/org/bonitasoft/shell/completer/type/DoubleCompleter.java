package org.bonitasoft.shell.completer.type;

import java.util.List;

import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;

/**
 * Created by baptiste on 08/07/14.
 */
public class DoubleCompleter implements TypeHandler<Double> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public Double getValue(String argument) {
            return Double.valueOf(argument);
    }

    @Override
    public boolean isCastableTo(String argument) {
        try{
            Double.valueOf(argument);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getString(Double result) {
        return String.valueOf(result);
    }
}
