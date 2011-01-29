/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterRepository {

  private Map<AdapterType, List<Object>> adaptersByType = new HashMap<AdapterType, List<Object>>();

  Object newInstance(Class adapterClass) {
    AdapterType adapterType = new AdapterType(adapterClass);
    Object adapterInstance = adapterType.newInstance();
    if ( !adaptersByType.containsKey(adapterType)) {
      adaptersByType.put(adapterType, new ArrayList<Object>());
    }
    adaptersByType.get(adapterType).add(adapterInstance);
    return adapterInstance;
  }

  public void inject(Object component) {
    for (AdapterType type : adaptersByType.keySet()) {
      if (type.hasMethodWithArgumentType(component.getClass())) {
        for (Object adapter : adaptersByType.get(type)) {
          type.inject(adapter, component);
        }
      }
    }
  }

}
