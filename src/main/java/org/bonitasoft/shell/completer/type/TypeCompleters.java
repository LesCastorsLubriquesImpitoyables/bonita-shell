package org.bonitasoft.shell.completer.type;

import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.type.BusinessArchiveTypeCompleter;

import java.util.HashMap;
import java.util.List;
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


    public static BonitaCompleter getCompleter(List<Class<?>> type) {
        for (Class<?> aClass : type) {
            if(completers.containsKey(aClass)){
                return completers.get(aClass);
            }
        }
        return null;
    }

    
}