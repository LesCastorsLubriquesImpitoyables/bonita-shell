package org.bonitasoft.shell.completer.type;

import java.io.File;
import java.util.List;

import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveFactory;
import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.CompletionHelper;

import jline.console.completer.FileNameCompleter;

/**
 * @author Baptiste Mesta
 */
public class BusinessArchiveTypeCompleter extends FileNameCompleter implements BonitaCompleter, TypeHandler<BusinessArchive> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return complete(commandLine.getLastArgument(), 0, candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public BusinessArchive getValue(String argument) {

        File file = new File(argument);

        try {
            return BusinessArchiveFactory.readBusinessArchive(file);
        } catch (Exception e) {
            throw new IllegalArgumentException("The parameter should point to a valid Business Archive: " + argument + ",issue is " + e.getMessage());
        }

    }

    @Override
    public boolean isCastableTo(String argument) {
        return new File(argument).isFile();
    }

    @Override
    public String getString(BusinessArchive result) {
        return String.valueOf(result);
    }
}
