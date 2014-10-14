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
package org.bonitasoft.shell.completer.reflect;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.BonitaCompleter;
import org.bonitasoft.shell.completer.type.TypeCompleters;
import org.bonitasoft.shell.completer.type.TypeHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Baptiste Mesta
 */
public class ReflectCommand extends ShellCommand {

    private static final Pattern METHOD_REGEX = Pattern.compile("([a-zA-Z_0-9]+)\\((.*)\\)");

    private static final Pattern PARAM_REGEX = Pattern.compile("([a-zA-Z_0-9\\[\\]]+)(<.*>)?.([a-zA-Z_0-9]+)");

    private final String apiName;

    private final Method[] methods;

    private final ArrayList<String> methodNames;

    private final Map<String, List<Method>> methodMap;

    private final Map<Method, MethodHelp> methodHelpMap;

    public ReflectCommand(final Class<?> apiClass) {
        this.apiName = apiClass.getSimpleName();
        methods = apiClass.getMethods();
        this.methodMap = new HashMap<String, List<Method>>();
        final HashSet<String> hashSet = new HashSet<String>();
        for (final Method m : methods) {
            final String methodName = m.getName();
            hashSet.add(methodName);
            if (!methodMap.containsKey(methodName)) {
                methodMap.put(methodName, new ArrayList<Method>());
            }
            methodMap.get(methodName).add(m);
        }

        methodNames = new ArrayList<String>(hashSet);
        Collections.sort(methodNames);
        methodHelpMap = retrieveMethodHelpFromSources(apiClass, methodMap);
    }

    private Map<Method, MethodHelp> retrieveMethodHelpFromSources(final Class<?> apiClass, final Map<String, List<Method>> methodMap) {
        final List<Class<?>> allSuperClass = getAllSuperClass(apiClass);
        final HashMap<Method, MethodHelp> helpHashMap = new HashMap<Method, MethodHelp>();
        final List<Document> javadocClasses = getJavadocClasses(allSuperClass);
        for (final Document javadocClass : javadocClasses) {
            final Elements methods = javadocClass.select(".overviewSummary td.colLast");
            for (final Element element : methods) {
                final String description = element.select(".block").text();
                final String methodBlock = element.select("code").first().text();
                final Matcher matcherMethod = METHOD_REGEX.matcher(methodBlock);
                while (matcherMethod.find()) {
                    final String name = matcherMethod.group(1);
                    String param = matcherMethod.group(2);
                    param = param.replace("...", "[]");
                    final String[] params = param.split(", ");
                    final ArrayList<String> parameterNames = new ArrayList<String>();
                    final ArrayList<String> parameterTypes = new ArrayList<String>();
                    for (int i = 0; i < params.length; i++) {
                        final Matcher matcherParam = PARAM_REGEX.matcher(params[i]);
                        while (matcherParam.find()) {
                            parameterTypes.add(matcherParam.group(1));
                            parameterNames.add(matcherParam.group(3));
                        }
                    }
                    Method methodOfDeclaration = getMethodOfDeclaration(methodMap, name, parameterTypes);
                    if(methodOfDeclaration != null){
                        //not in the binaries but in the javadoc..
                        helpHashMap.put(methodOfDeclaration, new MethodHelp(description, parameterNames));
                    }
                }
            }
        }
        return helpHashMap;
    }

    private Method getMethodOfDeclaration(final Map<String, List<Method>> methodMap, final String name, final List<String> parameterTypesAsString) {
        final List<Method> methodList = methodMap.get(name);
        if (methodList != null){
            for (final Method method : methodList) {
                final Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == parameterTypesAsString.size()) {
                    boolean isOk = true;
                    for (int i = 0; i < parameterTypes.length; i++) {
                        final String typeParameter = parameterTypesAsString.get(i);
                        final Class<?> parameterType = parameterTypes[i];

                        if (!parameterType.getSimpleName().equals(typeParameter)) {
                            isOk = false;
                            break;
                        }
                    }
                    if (isOk) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    private List<Document> getJavadocClasses(final List<Class<?>> allSuperClass) {
        final List<Document> javadoc = new ArrayList<Document>();
        for (final Class<?> superClass : allSuperClass) {
            try {
                javadoc.add(Jsoup.parse(superClass.getResourceAsStream("/" + superClass.getName().replace(".", "/") + ".html"), null, "/"));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return javadoc;
    }

    private List<Class<?>> getAllSuperClass(final Class<?> apiClass) {
        final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        classes.addAll(Arrays.asList(apiClass.getInterfaces()));
        classes.add(apiClass);
        return classes;
    }

    @Override
    public String getName() {
        return apiName;
    }

    @Override
    public boolean execute(final List<String> args, final ShellContext context) throws Exception {
        final Object api = context.getApi(apiName);
        final String methodName = args.get(0);
        final List<String> parameters = args.subList(1, args.size());
        final List<Method> methods = getMethods(methodName, this.methods, parameters);
        final Iterator<Method> iterator = methods.iterator();
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

    private List<Method> getMethods(final String methodName, final Method[] methods, final List<String> parameters) {
        final List<Method> possibleMethods = new ArrayList<Method>();

        for (final Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (method.getParameterTypes().length == parameters.size()) {
                    possibleMethods.add(method);
                }
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
        if (!possibleMethods.isEmpty()) {
            return possibleMethods;
        }
        throw new IllegalArgumentException("method does not exists");
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
        final List<Method> possibleMethods = methodMap.get(methodName);
        String help = "";
        if (possibleMethods != null) {
            for (final Method possibleMethod : possibleMethods) {
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

    public List<Class<?>> getArgumentType(final String methodName, final int index) {
        final List<Method> methods = methodMap.get(methodName);
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
