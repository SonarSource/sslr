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

import com.sonar.sslr.api.AstNode;

class AdapterRepository {

  private Map<AdapterType, List<Object>> adaptersByType = new HashMap<AdapterType, List<Object>>();
  private Map<AstNode, Object> adapterByAstNode = new HashMap<AstNode, Object>();
  private Map<Class, AdapterType> adapterTypeByClass = new HashMap<Class, AdapterType>();

  Object plug(Class adapterClass, AstNode node) {
    AdapterType adapterType = new AdapterType(adapterClass);
    Object adapterInstance = adapterType.newInstance();
    plug(adapterInstance, node);
    return adapterInstance;
  }

  void plug(Object adapterInstance, AstNode node) {
    Class adapterClass = adapterInstance.getClass();
    if ( !adapterTypeByClass.containsKey(adapterClass)) {
      AdapterType adapterType = new AdapterType(adapterClass);
      adaptersByType.put(adapterType, new ArrayList<Object>());
      adapterTypeByClass.put(adapterClass, adapterType);
    }
    AdapterType adapterType = adapterTypeByClass.get(adapterClass);
    adaptersByType.get(adapterType).add(adapterInstance);
    adapterByAstNode.put(node, adapterInstance);
  }

  void inject(Object component) {
    for (AdapterType type : adaptersByType.keySet()) {
      if (type.hasMethodWithArgumentType(component.getClass())) {
        for (Object adapter : adaptersByType.get(type)) {
          type.inject(adapter, component);
        }
      }
    }
  }

  void injectAdapter(AstNode parentNode, AstNode node) {
    Object parentAdapter = adapterByAstNode.get(parentNode);
    Object adapter = adapterByAstNode.get(node);
    if (parentAdapter != null && adapter != null) {
      injectAdapter(parentAdapter, adapter);
    }
    if (parentAdapter == null && adapter != null && parentNode.getNumberOfChildren() == 1) {
      plug(adapter, parentNode);
    }
  }

  void injectAdapter(Object parentAdapter, Object adapter) {
    AdapterType parentAdapterType = adapterTypeByClass.get(parentAdapter.getClass());
    if (parentAdapterType.hasMethodWithArgumentType(adapter.getClass())) {
      parentAdapterType.inject(parentAdapter, adapter);
    }
  }
}
