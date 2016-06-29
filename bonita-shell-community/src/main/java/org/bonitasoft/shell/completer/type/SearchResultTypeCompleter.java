package org.bonitasoft.shell.completer.type;

import java.io.Serializable;
import java.util.List;

import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.CompletionHelper;
import org.bonitasoft.shell.completer.reflect.ReflectMethodCommand;

import jline.console.completer.FileNameCompleter;

/**
 * Created by baptiste on 08/07/14.
 */
public class SearchResultTypeCompleter extends FileNameCompleter implements BonitaCompleter, TypeHandler<SearchResult<?>> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return complete(commandLine.getLastArgument(), 0, candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public SearchResult<?> getValue(String argument) {
        return null;
    }

    @Override
    public boolean isCastableTo(String argument) {
        return false;
    }

    @Override
    public String getString(SearchResult<?> result) {
        String print = "Search result (" + result.getCount() + "):\n";
        List<? extends Serializable> elements = result.getResult();
        for (Serializable element : elements) {
            print += " * " + ReflectMethodCommand.printResult(element) + "\n";
        }
        return print;
    }
}
