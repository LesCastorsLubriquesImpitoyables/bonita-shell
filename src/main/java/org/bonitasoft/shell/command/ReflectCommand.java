/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 ** 
 * @since 6.2
 */
package org.bonitasoft.shell.command;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.reflect.ReflectMethodArgumentCompleter;
import org.bonitasoft.shell.completer.reflect.ReflectMethodCompleter;
import org.bonitasoft.shell.completer.type.BusinessArchiveTypeCompleter;
import org.bonitasoft.shell.completer.type.TypeCompleters;

/**
 * @author Baptiste Mesta
 */
public class ReflectCommand extends ShellCommand {

    private final String apiName;

    private final Method[] methods;

    private final ArrayList<String> methodNames;

    private final Map<String, List<Method>> methodMap = new HashMap<String, List<Method>>();

    public ReflectCommand(final String apiName, final Class<?> apiClass) {
        this.apiName = apiName;
        methods = apiClass.getMethods();
        HashSet<String> hashSet = new HashSet<String>();
        for (Method m : methods) {
            String methodName = m.getName();
            hashSet.add(methodName);
            if (!methodMap.containsKey(methodName)) {
                methodMap.put(methodName, new ArrayList<Method>());
            }
            methodMap.get(methodName).add(m);
        }
        methodNames = new ArrayList<String>(hashSet);
        Collections.sort(methodNames);
    }

    @Override
    public String getName() {
        return apiName;
    }

    @Override
    public boolean execute(final List<String> args, final ShellContext context) throws Exception {
        Object api = context.getApi(apiName);
        String methodName = args.get(0);
        List<String> parameters = args.subList(1, args.size());
        List<Method> methods = getMethods(methodName, this.methods, parameters);
        Iterator<Method> iterator = methods.iterator();
        while (iterator.hasNext()) {
            Method method = iterator.next();
            try {
                if (method != null) {
                    Object result = invokeMethod(api, method, parameters);
                    //TODO handle return type
                    System.out.println(result);
                }
            } catch (Exception e) {
                if (!iterator.hasNext()) {
                    throw e;
                }
            }
        }

        return false;
    }

    /**
     * @param api
     * @param method
     * @param parameters
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private Object invokeMethod(final Object api, final Method method, final List<String> parameters) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        return method.invoke(api, castParameters(method.getParameterTypes(), parameters));
    }

    /**
     * @param classes
     * @param parameters
     * @return
     */
    private Object[] castParameters(final Class<?>[] classes, final List<String> parameters) {
        List<Object> list = new ArrayList<Object>(parameters.size());
        for (int i = 0; i < classes.length; i++) {
            Class<?> clazz = classes[i];
            String parameter = parameters.get(i);
            Serializable casted;
            casted = getCastedParameter(clazz, parameter);
            list.add(casted);
        }
        return list.toArray();
    }

    private Serializable getCastedParameter(Class<?> parameterClass, String parameter) {
        if ("null".equals(parameter)) {
            return null;
        } else {
            return (Serializable) TypeCompleters.getCompleter(parameterClass).getValue(parameter);
        }

    }

    private List<Method> getMethods(final String methodName, final Method[] methods, final List<String> parameters) {
        List<Method> possibleMethods = new ArrayList<Method>();

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (method.getParameterTypes().length == parameters.size()) {
                    possibleMethods.add(method);
                }
            }
        }
        Iterator<Method> iterator = possibleMethods.iterator();
        while (iterator.hasNext()) {
            Method next = iterator.next();
            Class<?>[] parameterTypes = next.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                String parameterAsString = parameters.get(i);
                if (!isCastableTo(parameterAsString, parameterType)) {
                    iterator.remove();
                    break;
                }
            }
        }
        if (!possibleMethods.isEmpty()) {
            return possibleMethods;
        }
        throw new IllegalArgumentException("method does not exists");
    }

    private boolean isCastableTo(String parameterAsString, Class<?> parameterType) {
        return TypeCompleters.getCompleter(parameterType).isCastableTo(parameterAsString);
    }

    @Override
    public void printHelp() {
        PrintColor.printGreenBold(apiName + " <method name> <parameters>");

    }

    @Override
    public boolean validate(final List<String> args) {
        return true;
    }

    @Override
    public List<BonitaCompleter> getCompleters() {
        return Arrays.<BonitaCompleter> asList(new ReflectMethodCompleter(this), new ReflectMethodArgumentCompleter(this));
    }

    /**
     * @return
     */
    public List<String> getMethodNames() {
        return methodNames;
    }

    /**
     * @param methodName
     */
    public String getMethodHelp(final String methodName) {
        List<Method> list = methodMap.get(methodName);
        if (list != null) {
            String help = "";
            for (Method method : list) {
                help += methodName + "(";
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (Class<?> class1 : parameterTypes) {
                    help += class1.getSimpleName() + ", ";
                }
                help += ")\n";
            }
            return help;
        }
        return null;
    }

    public List<Class<?>> getArgumentType(String methodName, int index) {
        List<Method> methods = methodMap.get(methodName);
        if (methods == null) {
            return null;
        }

        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (index < parameterTypes.length) {
                classes.add(parameterTypes[index]);
            }
        }

        return classes;
    }

}
