/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import java.lang.reflect.Field;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleImpl;

public class GrammarFieldsInitializer {

  public static void initializeRuleFields(Object rules, Class<?> grammar) {
    Field[] fields = grammar.getDeclaredFields();
    for (Field field : fields) {
      if (field.getType() == Rule.class) {
        String fieldName = field.getName();
        try {
          field.set(rules, new RuleImpl(fieldName));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
