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

package org.bonitasoft.shell.completer.type;

import java.util.List;

import org.bonitasoft.engine.bpm.BaseElement;
import org.bonitasoft.shell.completer.ArgumentParser;
import org.bonitasoft.shell.completer.CompletionHelper;

/**
 * @author Baptiste Mesta
 */
public class BaseElementTypeCompleter implements TypeHandler<BaseElement> {

    @Override
    public BaseElement getValue(String argument) {
        return null;
    }

    @Override
    public boolean isCastableTo(String argument) {
        return false;
    }

    @Override
    public String getString(BaseElement result) {
        return result.getClass() + "[id=" + result.getId() + "]";
    }

    @Override
    public int complete(ArgumentParser commandLine, List<CharSequence> candidates) {
        return 0;
    }

    @Override
    public CompletionHelper getCompletionHelper() {
        return null;
    }
}
