package org.bonitasoft.shell.completer.type;

import java.io.File;
import java.util.List;

import jline.console.completer.FileNameCompleter;

import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.CompletionHelper;

import com.bonitasoft.engine.bpm.bar.BusinessArchiveFactory;

/**
 * Created by baptiste on 08/07/14.
 */
public class ProcessDefinitionTypeCompleter extends FileNameCompleter implements BonitaCompleter, TypeHandler<ProcessDefinition> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return complete(commandLine.getLastArgument(), 0, candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public ProcessDefinition getValue(String argument) {
        return null;
    }

    @Override
    public boolean isCastableTo(String argument) {
        return false;
    }

    @Override
    public String getString(ProcessDefinition result) {
        return "Process definition: " + result.getName() + "--" + result.getVersion() + " (" + result.getId() + ")";
    }
}
