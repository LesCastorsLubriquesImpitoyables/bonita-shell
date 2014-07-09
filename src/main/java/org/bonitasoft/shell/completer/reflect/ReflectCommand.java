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

import java.io.InputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.type.ClassOrInterfaceType;
import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.ShellCommand;
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

    private final Map<String, List<Method>> methodMap;

    private final Map<Method, MethodHelp> methodHelpMap;


    public ReflectCommand(final Class<?> apiClass) {
        this.apiName = apiClass.getSimpleName();
        methods = apiClass.getMethods();
        this.methodMap = new HashMap<String, List<Method>>();
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
        methodHelpMap = retrieveMethodHelpFromSources(apiClass,methodMap);


    }

    private Map<Method, MethodHelp> retrieveMethodHelpFromSources(Class<?> apiClass, Map<String, List<Method>> methodMap) {
        List<Class<?>> allSuperClass = getAllSuperClass(apiClass);
        List<CompilationUnit> compilationUnits = getCompilationUnits(allSuperClass);
        HashMap<Method, MethodHelp> helpHashMap = new HashMap<Method, MethodHelp>();
        for (CompilationUnit compilationUnit : compilationUnits) {
            List<BodyDeclaration> members = compilationUnit.getTypes().get(0).getMembers();
            for (BodyDeclaration member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) member;
                    List<TypeParameter> parameters = methodDeclaration.getTypeParameters();
                    JavadocComment javaDoc = methodDeclaration.getJavaDoc();
                    ArrayList<String> parameterNames = new ArrayList<String>();
                    for (TypeParameter parameter : parameters) {
                        parameterNames.add(parameter.getName());
                    }
                    
                    helpHashMap.put(getMethodOfDeclaration(methodMap,methodDeclaration) ,new MethodHelp(javaDoc.getContent(),parameterNames));

                }
            }
        }

        return null;
    }

    private Method getMethodOfDeclaration(Map<String, List<Method>> methodMap, MethodDeclaration methodDeclaration) {
        List<Method> methodList = methodMap.get(methodDeclaration.getName());
        for (Method method : methodList) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            List<TypeParameter> typeParameters = methodDeclaration.getTypeParameters();
            if(parameterTypes.length == typeParameters.size()){
                boolean isOk = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    TypeParameter typeParameter = typeParameters.get(i);
                    Class<?> parameterType = parameterTypes[i];
                    List<ClassOrInterfaceType> typeBound = typeParameter.getTypeBound();
                    for (ClassOrInterfaceType classOrInterfaceType : typeBound) {
                        if (!classOrInterfaceType.getName().equals(parameterType.getName())){
                            isOk = false;
                            break;
                        }

                    }
                    if(!isOk){
                        break;
                    }
                }
                if(isOk){
                    return method;
                }
            }
        }
        throw new IllegalStateException("the method "+methodDeclaration.getName()+" does not exists in the type");
    }

    private List<CompilationUnit> getCompilationUnits(List<Class<?>> allSuperClass) {
        ArrayList<CompilationUnit> compilationUnitss = new ArrayList<CompilationUnit>(allSuperClass.size());
        for (Class<?> allSuperClas : allSuperClass) {
            InputStream resourceAsStream = allSuperClas.getResourceAsStream("/"+allSuperClas.getName().replace(".","/") + ".java");
            try {
                compilationUnitss.add(JavaParser.parse(resourceAsStream));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
        return compilationUnitss;
    }

    private List<Class<?>> getAllSuperClass(Class<?> apiClass) {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
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
