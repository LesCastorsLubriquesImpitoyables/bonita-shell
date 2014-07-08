package org.bonitasoft.shell.completer;

import org.bonitasoft.engine.bpm.bar.BusinessArchive;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baptiste on 08/07/14.
 */
public class TypeCompleters {


    private static Map<Class<?>,BonitaCompleter> completers;

    static {
        completers = new HashMap<Class<?>,BonitaCompleter>();
        completers.put(BusinessArchive.class,new BusinessArchiveTypeCompleter());
    }


    public static BonitaCompleter getCompleter(Class<?> type) {
        return completers.get(type);
    }
}
