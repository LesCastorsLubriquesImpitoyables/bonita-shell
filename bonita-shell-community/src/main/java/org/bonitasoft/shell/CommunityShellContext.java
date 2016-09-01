
/*
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.bonitasoft.shell;

import java.util.Map;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.PlatformAPIAccessor;
import org.bonitasoft.engine.api.PlatformLoginAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.PlatformSession;
import org.bonitasoft.engine.util.APITypeManager;

/**
 * @author Baptiste Mesta
 */
public class CommunityShellContext implements ShellContext {

    private PlatformLoginAPI platformLoginAPI;
    private LoginAPI loginAPI;
    private APISession session;
    private PlatformSession platformSession;
    private static ShellContext INSTANCE = new CommunityShellContext();

    private CommunityShellContext() {
    }

    public static ShellContext getInstance() {
        return INSTANCE;
    }

    /**
     * @return
     */
    @Override
    public boolean isLogged() {
        return session != null;
    }

    @Override
    public boolean isLoggedOnPlatform() {
        return platformSession != null;
    }

    @Override
    public void logout() throws Exception {
        if (session != null) {

            loginAPI.logout(session);
        }
        if (platformSession != null) {
            platformLoginAPI.logout(platformSession);
        }
        platformLoginAPI = null;
        loginAPI = null;
        session = null;
    }

    /**
     * @param username
     * @param password
     */
    @Override
    public void login(final String username, final String password) throws Exception {
        loginAPI = TenantAPIAccessor.getLoginAPI();
        platformLoginAPI = PlatformAPIAccessor.getPlatformLoginAPI();
        session = loginAPI.login(username, password);
    }

    @Override
    public void loginPlatform(final String username, final String password) throws Exception {
        loginAPI = TenantAPIAccessor.getLoginAPI();
        platformLoginAPI = PlatformAPIAccessor.getPlatformLoginAPI();
        platformSession = platformLoginAPI.login(username, password);
    }

    @Override
    @Deprecated
    public Object getApi(final String apiName) throws Exception {
        if (apiName.equals("ProcessAPI")) {
            return TenantAPIAccessor.getProcessAPI(session);
        }
        if (apiName.equals("IdentityAPI")) {
            return TenantAPIAccessor.getIdentityAPI(session);

        }
        if (apiName.equals("ProfileAPI")) {
            return TenantAPIAccessor.getProfileAPI(session);
        }
        if (apiName.equals("PageAPI")) {
            return TenantAPIAccessor.getCustomPageAPI(session);
        }
        if (apiName.equals("ThemeAPI")) {
            return TenantAPIAccessor.getThemeAPI(session);
        }
        if (apiName.equals("CommandAPI")) {
            return TenantAPIAccessor.getCommandAPI(session);
        }
        if (apiName.equals("PlatformAPI")) {
            return PlatformAPIAccessor.getPlatformAPI(platformSession);
        }
        if (apiName.equals("PlatformCommandAPI")) {
            return PlatformAPIAccessor.getPlatformCommandAPI(platformSession);
        }
        throw new IllegalArgumentException("Unknown API: " + apiName);
    }

    @Override
    public String getLoggedUser() {
        if (isLogged()) {
            return session.getUserName();
        }
        if (isLoggedOnPlatform()) {
            return platformSession.getUserName();
        }
        return "bonita";
    }

    @Override
    public void setConnectionParameters(String apiAccessType, Map<String, String> connectionParameters) {
        APITypeManager.setAPITypeAndParams(ApiAccessType.valueOf(apiAccessType), connectionParameters);
    }
}
