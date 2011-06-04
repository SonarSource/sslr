/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import java.util.HashMap;
import java.util.Map;

public class DslContext {

  private Map<String, Object> memory = new HashMap<String, Object>();

  public void put(String variableName, Object value) {
    memory.put(variableName, value);
  }

  public Object get(String variableName) {
    return memory.get(variableName);
  }
}
