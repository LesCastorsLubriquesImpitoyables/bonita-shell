package org.bonitasoft.shell.completer.type;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;

/**
 * @author Baptiste Mesta
 */
public class ListTypeCompleter implements TypeHandler<List<?>> {

    private Pattern pattern = Pattern.compile("( )*\\[.*\\]( )*");

    @Override
    public List<?> getValue(String argument) {
        String listAsString = argument.trim();
        listAsString = listAsString.substring(1, listAsString.length() - 1);
        String[] split = listAsString.split(",");
        return Arrays.asList(split);
    }

    @Override
    public boolean isCastableTo(String argument) {
        return pattern.matcher(argument).matches();
    }

    @Override
    public String getString(List<?> result) {
        String stringResult = "[";
        for (Object o : result) {
            TypeHandler<Object> completer = (TypeHandler<Object>) TypeCompleters.getCompleter(o.getClass());
            stringResult += completer.getString(o);
            stringResult += ", ";
        }
        if (!result.isEmpty()) {
            stringResult = stringResult.substring(0, stringResult.length() - 2);
        }
        stringResult += "]";
        return stringResult;
    }

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }
}
