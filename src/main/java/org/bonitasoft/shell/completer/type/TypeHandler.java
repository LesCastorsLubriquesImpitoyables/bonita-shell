package org.bonitasoft.shell.completer.type;

/**
 * Created by baptiste on 08/07/14.
 */
public interface TypeHandler<T> {


    T getValue(String argument) ;

}
