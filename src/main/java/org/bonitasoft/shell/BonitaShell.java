package org.bonitasoft.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class BonitaShell {

    private static final String SHELL_INITIALIZER = "shell.initializer";

    public static void main(final String[] args) throws Exception {
        CommandLine line = parseOptions(args);
        Properties config = loadProperties();
        addCommandLineOptions(line, config);
        ShellInitializer initializer = createInitializer(config);
        runShell(initializer);
    }

    private static void runShell(ShellInitializer initializer) throws Exception {
        final Shell shell = new Shell(initializer);
        shell.init();
        shell.run(System.in, System.out);
        shell.destroy();
    }

    private static ShellInitializer createInitializer(Properties config)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        String initializerClassName = config.getProperty(SHELL_INITIALIZER);
        if (initializerClassName == null) {
            System.out.println("No initializer class found in config.properties file, property name is " + SHELL_INITIALIZER);
            System.exit(1);
        }

        Class<?> initializerClass = Class.forName(initializerClassName);
        Constructor<?> constructor = initializerClass.getConstructor(Properties.class);
        return (ShellInitializer) constructor.newInstance(config);
    }

    private static CommandLine parseOptions(String[] args) {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();
        // create the Options
        Options options = new Options();
        options.addOption(new Option("h", "help", false, "print this message"));
        options.addOption(new Option("d", "debug", false, "print debugging information"));

        // parse the command line arguments
        CommandLine line = null;
        try {
            line = parser.parse(options, args, false);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }

        if (line.hasOption("help")) {
            printHelp(options);
            System.exit(0);
        }
        return line;
    }

    private static void printHelp(Options options) {
        String header = "Launch a shell to interact with the engine API\n\n";
        String footer = "\n";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("bonita-shell", header, options, footer, true);
    }

    private static void addCommandLineOptions(CommandLine line, Properties config) {
        if (line.hasOption("debug")) {
            System.out.println("debug mode enabled");
            config.setProperty("debug", "true");
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties config = new Properties();
        FileInputStream inStream = new FileInputStream(new File("../config.properties"));
        config.load(inStream);
        inStream.close();
        return config;
    }

}
