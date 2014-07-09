package org.bonitasoft.shell.completer.type;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jline.console.completer.FileNameCompleter;

import org.apache.commons.io.IOUtils;
import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;
import org.bonitasoft.shell.completer.ResolvingStringsCompleter;

/**
 * Created by baptiste on 08/07/14.
 */
public class BooleanCompleter extends ResolvingStringsCompleter implements TypeHandler<Boolean> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return complete(commandLine.getLastArgument(),0,candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public Boolean getValue(String argument) {
        return Boolean.valueOf(argument);
    }

    @Override
    public boolean isCastableTo(String argument) {
        return argument.equals("true")|| argument.equals("false");
    }

    @Override
    public List<String> resolveStrings() {
        return Arrays.asList("true","false");
    }

    @Override
    public String getString(Boolean result) {
        return String.valueOf(result);
    }
}
