package org.bonitasoft.shell.completer.type;

import org.bonitasoft.shell.completer.BonitaCompleter;

/**
 * Created by baptiste on 08/07/14.
 */
public interface TypeHandler<T> extends BonitaCompleter {


    T getValue(String argument);


    boolean isCastableTo(String argument);

}
