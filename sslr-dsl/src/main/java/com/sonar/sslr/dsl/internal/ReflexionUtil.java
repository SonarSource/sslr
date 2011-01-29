/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import java.lang.reflect.Method;

public class ReflexionUtil {

  public static boolean call(Object obj, String methodName) {
    try {
      Method method = obj.getClass().getMethod(methodName);
      method.invoke(obj);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static boolean hasMethod(Class obj, String methodName) {
    try {
      obj.getMethod(methodName);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static boolean call(Object obj, String methodName, String arg) {
    try {
      Method method = obj.getClass().getMethod(methodName, String.class);
      method.invoke(obj, arg);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

}
