package org.bonitasoft.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.bonitasoft.engine.api.ApiAccessType;

public class ShellConfiguration {

    static final String SHELL_FACTORY = "shell.factory";
    private final Properties config;
    private boolean debug;

    public ShellConfiguration() throws IOException {
        this.config = loadProperties();
    }

    private static Properties loadProperties() throws IOException {
        Properties config = new Properties();
        FileInputStream inStream = new FileInputStream(new File("../config.properties"));
        config.load(inStream);
        inStream.close();
        return config;
    }

    String getShellFactoryClassName() {
        return getConfig().getProperty(SHELL_FACTORY);
    }

    public Properties getConfig() {
        return config;
    }

    public Options getOptions() {
        Options options = new Options();
        options.addOption(new Option("h", "help", false, "print this message"));
        options.addOption(new Option("d", "debug", false, "print debugging information"));
        return options;
    }

    public void addCommandLineArguments(CommandLine line) {
        if (line.hasOption("debug")) {
            System.out.println("debug mode enabled");
            debug = true;
        }
    }

    private List<String> getCommaSeparatedValues(String key) {
        String apiClassesAsString = config.getProperty(key);
        String[] apiClasses = apiClassesAsString.replaceAll(" ", "").split(",");
        return Arrays.asList(apiClasses);
    }

    public List<String> getTenantAPIClassNames() {
        return getCommaSeparatedValues("API.tenant.classes");
    }

    public List<String> getPlatformAPIClassNames() {
        return getCommaSeparatedValues("API.platform.classes");
    }

    public boolean isColorActivated() {
        return Boolean.valueOf(config.getProperty("shell.color", "true"));
    }

    public String getDefaultUsername() {
        return config.getProperty("username");
    }

    public ApiAccessType getApiAccessType() {
        return ApiAccessType.valueOf(config.getProperty("org.bonitasoft.engine.api-type", "HTTP"));
    }

    public Map<String, String> getConnectionParameters() {

        HashMap<String, String> connectionProperties = new HashMap<>();
        putFromConf(connectionProperties, "application.name", "bonita");
        putFromConf(connectionProperties, "server.url", "http://localhost:8080");
        putFromConf(connectionProperties, "org.bonitasoft.engine.api-type.parameters", "server.url,application.name");

        return connectionProperties;
    }

    private void putFromConf(HashMap<String, String> connectionProperties, String key, String defaultValue) {
        connectionProperties.put(key, config.getProperty(key, defaultValue));
    }

    public boolean isDebug() {
        return debug;
    }
}
