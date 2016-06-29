package org.bonitasoft.shell;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class BonitaShell {

    public static void main(final String[] args) throws Exception {
        ShellConfiguration shellConfiguration = new ShellConfiguration();
        parseOptions(shellConfiguration, args);
        ShellFactory shellFactory = createFactory(shellConfiguration);
        runShell(shellFactory);
    }

    private static void runShell(ShellFactory shellFactory) throws Exception {
        final Shell shell = shellFactory.createShell();
        shell.run(System.in, System.out);
        shell.destroy();
    }

    private static ShellFactory createFactory(ShellConfiguration shellConfiguration)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        String initializerClassName = shellConfiguration.getShellFactoryClassName();
        if (initializerClassName == null) {
            System.out.println("No initializer class found in config.properties file, property name is " + ShellConfiguration.SHELL_FACTORY);
            System.exit(1);
        }

        Class<?> initializerClass = Class.forName(initializerClassName);
        ShellFactory shellFactory = (ShellFactory) initializerClass.newInstance();
        shellFactory.setConfiguration(shellConfiguration);
        return shellFactory;
    }

    private static void parseOptions(ShellConfiguration shellConfiguration, String[] args) {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();
        // create the Options
        Options options = shellConfiguration.getOptions();

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
        shellConfiguration.addCommandLineArguments(line);
    }

    private static void printHelp(Options options) {
        String header = "Launch a shell to interact with the engine API\n\n";
        String footer = "\n";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("bonita-shell", header, options, footer, true);
    }

}
