/*
 *
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
 *
 */

package org.bonitasoft.shell.completer.reflect;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Baptiste Mesta
 */
public class ReflectCommandFactory {


    private static final Pattern METHOD_REGEX = Pattern.compile("([a-zA-Z_0-9]+)\\((.*)\\)");

    private static final Pattern PARAM_REGEX = Pattern.compile("([a-zA-Z_0-9\\[\\]]+)(<.*>)?.([a-zA-Z_0-9]+)");

    public List<ReflectMethodCommand> createTenantCommands(List<String> classNames) throws Exception {
        return createCommands(classNames, false);
    }

    public List<ReflectMethodCommand> createPlatformCommands(List<String> classNames) throws Exception {
        return createCommands(classNames, true);
    }

    private List<ReflectMethodCommand> createCommands(List<String> classNames, boolean platform) throws ClassNotFoundException {
        ArrayList<ReflectMethodCommand> reflectCommands = new ArrayList<>();
        for (String className : classNames) {
            Class<?> apiClass = Class.forName(className);
            List<ReflectMethodCommand> commandsForClass = createCommandsForClass(apiClass);
            for (ReflectMethodCommand commandForClass : commandsForClass) {
                reflectCommands.add(commandForClass);
                commandForClass.setPlatform(platform);
            }
            reflectCommands.addAll(commandsForClass);
        }
        return reflectCommands;
    }

    private List<ReflectMethodCommand> createCommandsForClass(Class<?> apiClass) {
        String apiName = apiClass.getSimpleName();
        Method[] methods = apiClass.getMethods();
        HashMap<String, List<Method>> methodMap = new HashMap<>();
        for (final Method m : methods) {
            final String methodName = m.getName();
            if (!methodMap.containsKey(methodName)) {
                methodMap.put(methodName, new ArrayList<Method>());
            }
            methodMap.get(methodName).add(m);
        }

        ArrayList<String> methodNames = new ArrayList<>(methodMap.keySet());
        Collections.sort(methodNames);
        Map<Method, MethodHelp> methodHelpMap = retrieveMethodHelpFromSources(apiClass, methodMap);
        ArrayList<ReflectMethodCommand> reflectMethodCommands = new ArrayList<>();
        for (String methodName : methodMap.keySet()) {
            reflectMethodCommands.add(new ReflectMethodCommand(methodName, methodHelpMap, apiName, methodMap.get(methodName)));
        }
        return reflectMethodCommands;
    }


    private Map<Method, MethodHelp> retrieveMethodHelpFromSources(final Class<?> apiClass, final Map<String, List<Method>> methodMap) {
        final List<Class<?>> allSuperClass = getAllSuperClass(apiClass);
        final HashMap<Method, MethodHelp> helpHashMap = new HashMap<>();
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
                    final ArrayList<String> parameterNames = new ArrayList<>();
                    final ArrayList<String> parameterTypes = new ArrayList<>();
                    for (int i = 0; i < params.length; i++) {
                        final Matcher matcherParam = PARAM_REGEX.matcher(params[i]);
                        while (matcherParam.find()) {
                            parameterTypes.add(matcherParam.group(1));
                            parameterNames.add(matcherParam.group(3));
                        }
                    }
                    Method methodOfDeclaration = getMethodOfDeclaration(methodMap, name, parameterTypes);
                    if (methodOfDeclaration != null) {
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
        if (methodList != null) {
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
        final List<Document> javadoc = new ArrayList<>();
        for (final Class<?> superClass : allSuperClass) {
            try {
                javadoc.add(Jsoup.parse(superClass.getResourceAsStream("/" + superClass.getName().replace(".", "/") + ".html"), null, "/"));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return javadoc;
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

    private List<Class<?>> getAllSuperClass(final Class<?> apiClass) {
        final ArrayList<Class<?>> classes = new ArrayList<>();
        classes.addAll(getSuperClasses(apiClass));
        classes.add(apiClass);
        return classes;
    }
}
