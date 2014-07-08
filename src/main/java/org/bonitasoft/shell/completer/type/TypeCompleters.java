package org.bonitasoft.shell.completer.type;

import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.type.BusinessArchiveTypeCompleter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baptiste on 08/07/14.
 */
public class TypeCompleters {


    private static Map<Class<?>,TypeHandler<?>> completers;

    static {
        completers = new HashMap<Class<?>,TypeHandler<?>>();
        completers.put(BusinessArchive.class,new BusinessArchiveTypeCompleter());
        completers.put(byte[].class, new ByteArrayCompleter());
        completers.put(boolean.class, new BooleanCompleter());
        completers.put(Boolean.class, new BooleanCompleter());
        completers.put(Long.class, new LongCompleter());
        completers.put(long.class, new LongCompleter());
        completers.put(Double.class, new DoubleCompleter());
        completers.put(double.class, new DoubleCompleter());
        completers.put(Float.class, new FloatCompleter());
        completers.put(float.class, new FloatCompleter());
        completers.put(Integer.class, new IntegerCompleter());
        completers.put(int.class, new IntegerCompleter());
    }


    public static TypeHandler<?>  getCompleter(Class<?> type) {
        return getCompleter(Arrays.<Class<?>>asList(type));
    }

    public static TypeHandler<?> getCompleter(List<Class<?>> type) {
        for (Class<?> aClass : type) {
            if(completers.containsKey(aClass)){
                return completers.get(aClass);
            }
        }
        //in last case we return the string completer
        if (type.contains(String.class)){
            return new StringCompleter();
        }
        return null;
    }

    
}
