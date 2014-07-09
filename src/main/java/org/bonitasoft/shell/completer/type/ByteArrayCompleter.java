package org.bonitasoft.shell.completer.type;

import jline.console.completer.FileNameCompleter;
import org.apache.commons.io.IOUtils;
import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.CompletionHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by baptiste on 08/07/14.
 */
public class ByteArrayCompleter extends FileNameCompleter implements TypeHandler<byte[]> {

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return complete(commandLine.getLastArgument(),0,candidates);
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }

    @Override
    public byte[] getValue(String argument) {
        FileInputStream input = null;
        try {
            input = new FileInputStream(argument);
            return IOUtils.toByteArray(input);
        } catch (IOException e) {
            throw new IllegalArgumentException("The parameter should point to a valid file: " + argument);
        } finally {
            try {
                if(input != null) {
                    input.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isCastableTo(String argument) {
        return new File(argument).isFile();
    }

    @Override
    public String getString(byte[] result) {
        return String.valueOf(result);
    }
}
