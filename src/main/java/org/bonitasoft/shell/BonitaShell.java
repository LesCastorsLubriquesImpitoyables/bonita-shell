package org.bonitasoft.shell;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

public class BonitaShell {

    public static final String SHELL_INITIALIZER = "shell.initializer";

    public static void main(final String[] args) throws Exception {
        Properties config = new Properties();
        FileInputStream inStream = new FileInputStream(new File("../config.properties"));
        config.load(inStream);
        inStream.close();
        String initializerClassName = config.getProperty(SHELL_INITIALIZER);
        if (initializerClassName == null) {
            System.out.println("No initializer class found in config.properties file, property name is " + SHELL_INITIALIZER);
            return;
        }
        
        Class<?> initializerClass = Class.forName(initializerClassName);
        Constructor<?> constructor = initializerClass.getConstructor(Properties.class);
        final Shell shell = new Shell((ShellInitializer) constructor.newInstance(config));
        shell.init();
        shell.run(System.in, System.out);
        shell.destroy();
    }

}
