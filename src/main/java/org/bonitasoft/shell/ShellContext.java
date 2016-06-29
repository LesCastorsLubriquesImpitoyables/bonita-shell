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

import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.PlatformLoginAPI;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.PlatformSession;

import com.bonitasoft.engine.api.IdentityAPI;
import com.bonitasoft.engine.api.LoginAPI;
import com.bonitasoft.engine.api.MonitoringAPI;
import com.bonitasoft.engine.api.PageAPI;
import com.bonitasoft.engine.api.PlatformAPIAccessor;
import com.bonitasoft.engine.api.ProcessAPI;
import com.bonitasoft.engine.api.ProfileAPI;
import com.bonitasoft.engine.api.ReportingAPI;
import com.bonitasoft.engine.api.TenantAPIAccessor;
import com.bonitasoft.engine.api.ThemeAPI;

/**
 * @author Baptiste Mesta
 */
public class ShellContext {

    private static ShellContext INSTANCE = new ShellContext();
    private PlatformLoginAPI platformLoginAPI;
    private LoginAPI loginAPI;
    private APISession session;
    private PlatformSession platformSession;

    public static ShellContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShellContext();
        }
        return INSTANCE;
    }

    private ShellContext() {
    }

    /**
     * @return
     */
    public boolean isLogged() {
        return session != null;
    }

    public boolean isLoggedOnPlatform() {
        return platformSession != null;
    }

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
    public void login(final String username, final String password) throws Exception {
        loginAPI = TenantAPIAccessor.getLoginAPI();
        platformLoginAPI = PlatformAPIAccessor.getPlatformLoginAPI();
        session = loginAPI.login(username, password);
    }

    public void loginPlatform(final String username, final String password) throws Exception {
        loginAPI = TenantAPIAccessor.getLoginAPI();
        platformLoginAPI = PlatformAPIAccessor.getPlatformLoginAPI();
        platformSession = platformLoginAPI.login(username, password);
    }

    public IdentityAPI getIdentityAPI() throws BonitaException {
        return TenantAPIAccessor.getIdentityAPI(session);

    }

    public ProcessAPI getProcessAPI() throws BonitaException {
        return TenantAPIAccessor.getProcessAPI(session);
    }

    public ProfileAPI getProfileAPI() throws BonitaException {
        return TenantAPIAccessor.getProfileAPI(session);
    }

    public PageAPI getPageAPI() throws BonitaException {
        return TenantAPIAccessor.getPageAPI(session);
    }

    public MonitoringAPI getMonitoringAPI() throws BonitaException {
        return TenantAPIAccessor.getMonitoringAPI(session);
    }

    public ThemeAPI getThemeAPI() throws BonitaException {
        return TenantAPIAccessor.getThemeAPI(session);
    }

    public ReportingAPI getReportingAPI() throws BonitaException {
        return TenantAPIAccessor.getReportingAPI(session);
    }

    public CommandAPI getCommandAPI() throws BonitaException {
        return TenantAPIAccessor.getCommandAPI(session);
    }

    @Deprecated
    public Object getApi(final String apiName) throws Exception {
        if (apiName.equals("ProcessAPI")) {
            return getProcessAPI();
        }
        if (apiName.equals("IdentityAPI")) {
            return getIdentityAPI();
        }
        if (apiName.equals("ProfileAPI")) {
            return getProfileAPI();
        }
        if (apiName.equals("PageAPI")) {
            return getPageAPI();
        }
        if (apiName.equals("MonitoringAPI")) {
            return getMonitoringAPI();
        }
        if (apiName.equals("ThemeAPI")) {
            return getThemeAPI();
        }
        if (apiName.equals("ReportingAPI")) {
            return getReportingAPI();
        }
        if (apiName.equals("CommandAPI")) {
            return getCommandAPI();
        }
        if (apiName.equals("PlatformAPI")) {
            return PlatformAPIAccessor.getPlatformAPI(platformSession);
        }
        throw new IllegalArgumentException("Unknown API: " + apiName);
    }

    public String getLoggedUser() {
        if (isLogged()) {
            return session.getUserName();
        }
        if (isLoggedOnPlatform()) {
            return platformSession.getUserName();
        }
        return "bonita";
    }
}
