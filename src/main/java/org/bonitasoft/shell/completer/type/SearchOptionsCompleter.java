package org.bonitasoft.shell.completer.type;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;
import org.bonitasoft.shell.completer.ResolvingStringsCompleter;

/**
 * Created by baptiste on 08/07/14.
 */
public class SearchOptionsCompleter implements TypeHandler<SearchOptions> {


    private Pattern pattern = Pattern.compile("([0-9]+),([0-9]+)");

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public SearchOptions getValue(String argument) {
        String[] split = argument.split(",");
        return new SearchOptionsBuilder(Integer.valueOf(split[0]),Integer.valueOf(split[1])).done();
    }

    @Override
    public boolean isCastableTo(String argument) {
        return pattern.matcher(argument).matches();
    }

    @Override
    public String getString(SearchOptions result) {
        return String.valueOf(result);
    }
}
