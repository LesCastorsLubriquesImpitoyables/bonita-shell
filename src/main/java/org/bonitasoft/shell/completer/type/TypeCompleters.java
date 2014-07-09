package org.bonitasoft.shell.completer.type;

import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.type.BusinessArchiveTypeCompleter;

import java.util.*;

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
        completers.put(SearchOptions.class, new SearchOptionsCompleter());
        completers.put(ProcessDefinition.class, new ProcessDefinitionTypeCompleter());
        completers.put(SearchResult.class, new SearchResultTypeCompleter());
        completers.put(ProcessDeploymentInfo.class, new ProcessDeploymentInfoTypeCompleter());
    }


    public static TypeHandler<?>  getCompleter(Class<?> type) {
        return getCompleter(Arrays.<Class<?>>asList(type));
    }

    public static TypeHandler<?> getCompleter(List<Class<?>> type) {

        //firstPass class
        for (Class<?> aClass : type) {
            if(completers.containsKey(aClass)){
                return completers.get(aClass);
            }
        }

        //second pass: super classes
        for (Class<?> aClass : type) {
            List<Class<?>> superClasses = getSuperClasses(aClass);
            for (Class<?> superClass : superClasses) {
                if(completers.containsKey(superClass)){
                    return completers.get(superClass);
                }
            }
        }

        //in last case we return the string completer
        if (type.contains(String.class)){
            return new StringCompleter();
        }
        return null;
    }

    private static List<Class<?>> getSuperClasses(Class<?> aClass) {
        ArrayList<Class<?>> superClasses = new ArrayList<Class<?>>();
        List<Class<?>> interfaces = Arrays.asList(aClass.getInterfaces());
        for (Class<?> anInterface : interfaces) {
            superClasses.addAll(getSuperClasses(anInterface));
        }
        superClasses.addAll(interfaces);
        Class<?> superclass = aClass.getSuperclass();
        if (superclass != null){
            superClasses.addAll(getSuperClasses(superclass));
            superClasses.add(superclass);
        }
        return superClasses;
    }


}
