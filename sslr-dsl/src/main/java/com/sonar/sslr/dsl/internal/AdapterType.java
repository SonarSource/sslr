/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.sonar.sslr.dsl.DslException;

class AdapterType {

  private final Class adapterClass;
  private Map<Class, Method> methodsWithOneArgument = new HashMap<Class, Method>();

  AdapterType(Class adapterClass) {
    this.adapterClass = adapterClass;
    indexMethodsWithOneArgument();
  }

  Object newInstance() {
    try {
      return adapterClass.newInstance();
    } catch (Exception e) {
      throw new DslException("Unable to instanciate DSL adapter '" + adapterClass.getName() + "'", e);
    }
  }

  private void indexMethodsWithOneArgument() {
    for (Method method : adapterClass.getMethods()) {
      Class[] parameters = method.getParameterTypes();
      if (parameters.length == 1) {
        methodsWithOneArgument.put(parameters[0], method);
      }
    }
  }

  boolean hasMethodWithArgumentType(Class argumentType) {
    if (methodsWithOneArgument.containsKey(argumentType)) {
      return true;
    }
    Method method = getMethodAmongParents(argumentType);
    if (method != null) {
      methodsWithOneArgument.put(argumentType, method);
      return true;
    }
    return false;
  }

  private Method getMethodAmongParents(Class argumentType) {
    Class argumentTypeParent = argumentType.getSuperclass();
    if (argumentTypeParent == null) {
      return null;
    }
    Method method = methodsWithOneArgument.get(argumentTypeParent);
    if (method != null && argumentTypeParent != Object.class) {
      return method;
    }
    for (Class implementedInterface : argumentType.getInterfaces()) {
      method = methodsWithOneArgument.get(implementedInterface);
      if (method != null) {
        return method;
      }
      method = getMethodAmongParents(implementedInterface);
      if (method != null) {
        return method;
      }
    }
    return getMethodAmongParents(argumentTypeParent);
  }

  public int hashCode() {
    return adapterClass.hashCode();
  }

  public boolean equals(Object obj) {
    if (obj instanceof AdapterType) {
      return adapterClass.equals(((AdapterType) obj).adapterClass);
    }
    return false;
  }

  void inject(Object adapter, Object component) {
    Method method = methodsWithOneArgument.get(component.getClass());
    try {
      method.invoke(adapter, component);
    } catch (Exception e) {
      throw new DslException("Unable to call method " + adapterClass.getClass().getName() + "." + method.getName() + "("
          + component.getClass().getName() + " component)", e);
    }

  }
}
