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

package org.bonitasoft.shell.completer.reflect;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.type.TypeCompleters;
import org.bonitasoft.shell.completer.type.TypeHandler;

/**
 * @author Baptiste Mesta
 */
public class ReflectMethodCommand extends ShellCommand {

    private final String methodName;
    private final Map<Method, MethodHelp> methodHelpMap;
    private final String apiName;
    private List<Method> methods;

    public ReflectMethodCommand(String methodName, Map<Method, MethodHelp> methodHelpMap, String apiName, List<Method> methods) {
        this.methodName = methodName;
        this.methodHelpMap = methodHelpMap;
        this.apiName = apiName;
        this.methods = methods;
                        }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public boolean execute(List<String> parameters, ShellContext context) throws Exception {
        final Object api = context.getApi(apiName);
        final List<Method> methods = getMethods(parameters);
        final Iterator<Method> iterator = methods.iterator();
        if(methods.isEmpty()){
            PrintColor.printRedBold("No matching method found, try with other arguments");
            printHelp();
        }
        while (iterator.hasNext()) {
            final Method method = iterator.next();
            try {
                if (method != null) {
                    final Object result = invokeMethod(api, method, parameters);
                    PrintColor.printGreenBold(printResult(result));
                    return true;
                }
            } catch (Throwable e) {
                if (!iterator.hasNext()) {
                    if (e instanceof InvocationTargetException) {
                        final InvocationTargetException invocationTargetException = (InvocationTargetException) e;
                        e = invocationTargetException.getTargetException();
                    }
                        PrintColor.printRedBold(e.getMessage());

                    }
                }
            }
        }
        return false;
    }

    @Override
    public void printHelp() {
        System.out.println(getMethodHelp());
    }

    @Override
    public boolean validate(List<String> args) {
        return true;
    }

    public static String printResult(final Object result) {
        String string;
        if (result == null) {
            return "done";
        }
        final TypeHandler completer = TypeCompleters.getCompleter(result.getClass());
        if (completer != null) {
            string = completer.getString(result);
        } else {
            string = String.valueOf(result);
        }
        return string;
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
        final List<Object> list = new ArrayList<Object>(parameters.size());
        for (int i = 0; i < classes.length; i++) {
            final Class<?> clazz = classes[i];
            final String parameter = parameters.get(i);
            Serializable casted;
            casted = getCastedParameter(clazz, parameter);
            list.add(casted);
        }
        return list.toArray();
    }

    private Serializable getCastedParameter(final Class<?> parameterClass, final String parameter) {
        if ("null".equals(parameter)) {
            return null;
        } else {
            return (Serializable) TypeCompleters.getCompleter(parameterClass).getValue(parameter);
        }

    }

    private List<Method> getMethods(final List<String> parameters) {
        final List<Method> possibleMethods = new ArrayList<Method>();

        for (final Method method : methods) {
            if (method.getParameterTypes().length == parameters.size()) {
                possibleMethods.add(method);
            }
        }
        final Iterator<Method> iterator = possibleMethods.iterator();
        while (iterator.hasNext()) {
            final Method next = iterator.next();
            final Class<?>[] parameterTypes = next.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                final Class<?> parameterType = parameterTypes[i];
                final String parameterAsString = parameters.get(i);
                if (!isCastableTo(parameterAsString, parameterType)) {
                    iterator.remove();
                    break;
                }
            }
        }
        return possibleMethods;
    }

    private boolean isCastableTo(final String parameterAsString, final Class<?> parameterType) {
        if(!parameterType.isPrimitive() && parameterAsString.equals("null")){
            return true;
        }
        final TypeHandler<?> completer = TypeCompleters.getCompleter(parameterType);
        if (completer == null) {
            return false;
        }
        return completer.isCastableTo(parameterAsString);
    }

    @Override
    public List<BonitaCompleter> getCompleters() {
        return Arrays.<BonitaCompleter> asList(new ReflectMethodArgumentCompleter(this));
    }

    public String getMethodHelp() {
        String help = "";
        if (methods != null) {
            for (final Method possibleMethod : methods) {
                final MethodHelp methodHelp = methodHelpMap.get(possibleMethod);
                if (methodHelp != null) {
                    final List<String> argumentNames = methodHelp.getArgumentNames();
                    help += possibleMethod.getName() + "(";
                    final Class<?>[] parameterTypes = possibleMethod.getParameterTypes();
                    for (int i = 0; i < argumentNames.size(); i++) {
                        help += PrintColor.getGreenBold(parameterTypes[i].getSimpleName()) + " " + PrintColor.getGreen(argumentNames.get(i));
                        if (i < argumentNames.size() - 1) {
                            help += ", ";
                        }
                    }
                    help += ")\n";
                    help += methodHelp.getComment();
                    help += "\n\n";
                }
            }
        }
        return help;
    }

    public List<Class<?>> getArgumentType(final int index) {
        if (methods == null) {
            return null;
        }

        final List<Class<?>> classes = new ArrayList<Class<?>>();
        for (final Method method : methods) {
            final Class<?>[] parameterTypes = method.getParameterTypes();
            if (index < parameterTypes.length) {
                classes.add(parameterTypes[index]);
            }
        }

        return classes;
    }
}
