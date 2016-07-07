package org.bonitasoft.shell.completer.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baptiste on 08/07/14.
 */
public class TypeCompleters {

    private static Map<Class<?>, TypeHandler<?>> completers;

    static {
        completers = new HashMap<>();
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
        completers.put(List.class, new ListTypeCompleter());
        completers.put(String.class, new StringCompleter());
    }

    public static TypeHandler<?> getCompleter(Class<?> type) {
        return getCompleter(Collections.<Class<?>> singletonList(type));
    }

    public static TypeHandler<?> getCompleter(List<Class<?>> type) {

        //firstPass class
        for (Class<?> aClass : type) {
            if (completers.containsKey(aClass)) {
                return completers.get(aClass);
            }
        }

        //second pass: super classes
        for (Class<?> aClass : type) {
            List<Class<?>> superClasses = getSuperClasses(aClass);
            for (Class<?> superClass : superClasses) {
                if (completers.containsKey(superClass)) {
                    return completers.get(superClass);
                }
            }
        }

        //in last case we return the groovy completer
        return new GroovyCompleter();
    }

    public static void addCompleter(Class<?> type, TypeHandler<?> typeHandler) {
        completers.put(type, typeHandler);
    }

    private static List<Class<?>> getSuperClasses(Class<?> aClass) {
        ArrayList<Class<?>> superClasses = new ArrayList<>();
        List<? extends Class<?>> interfaces = Arrays.asList(aClass.getInterfaces());
        for (Class<?> anInterface : interfaces) {
            superClasses.addAll(getSuperClasses(anInterface));
        }
        superClasses.addAll(interfaces);
        Class<?> superclass = aClass.getSuperclass();
        if (superclass != null) {
            superClasses.addAll(getSuperClasses(superclass));
            superClasses.add(superclass);
        }
        return superClasses;
    }

}
