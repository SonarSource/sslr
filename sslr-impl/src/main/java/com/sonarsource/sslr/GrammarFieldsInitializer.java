/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.sslr;

import java.lang.reflect.Field;

import com.sonarsource.sslr.api.Grammar;
import com.sonarsource.sslr.api.Rule;
import com.sonarsource.sslr.matcher.RuleImpl;

public class GrammarFieldsInitializer {

  public static void initializeRuleFields(Grammar grammarImpl, Class<?> grammar) {
    Field[] fields = grammar.getDeclaredFields();
    for (Field field : fields) {
      if (field.getType() == Rule.class) {
        String fieldName = field.getName();
        try {
          field.set(grammarImpl, new RuleImpl(fieldName));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
