package org.bonitasoft.shell;

import java.util.Map;

/**
 * @author Baptiste Mesta
 */
public interface ShellContext {

    boolean isLogged();

    boolean isLoggedOnPlatform();

    void logout() throws Exception;

    void login(String username, String password) throws Exception;

    void loginPlatform(String username, String password) throws Exception;

    @Deprecated
    Object getApi(String apiName) throws Exception;

    String getLoggedUser();

    void setConnectionParameters(String apiAccessType, Map<String, String> connectionParameters);
}
