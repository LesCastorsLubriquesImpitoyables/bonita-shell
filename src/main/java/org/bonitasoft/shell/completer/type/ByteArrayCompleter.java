package org.bonitasoft.shell.completer.type;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import jline.console.completer.FileNameCompleter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;

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
        try {
            File tempFile = File.createTempFile("shell-result", ".tmp");
            FileUtils.writeByteArrayToFile(tempFile, result);
            return "Result was saved to "+tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(result);
    }
}
