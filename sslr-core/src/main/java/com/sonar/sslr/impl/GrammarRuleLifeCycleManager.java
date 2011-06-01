/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import java.lang.reflect.Field;

import org.apache.commons.lang.ClassUtils;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleDefinition;

/**
 * Utility class for handling grammar rule life cycle
 */
public final class GrammarRuleLifeCycleManager {

  private GrammarRuleLifeCycleManager() {
  }

  /**
   * Notify the rules of the given grammar that a parsing has just ended so that they can optionally reinitialize their state.
   * 
   * @param rules
   *          the grammar object that contains the rules to notify
   */
  static void notifyEndParsing(Object grammar) {
    Field[] fields = grammar.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (ClassUtils.isAssignable(field.getType(), Rule.class)) {
        try {
          Object rule = field.get(grammar);
          ((RuleDefinition) rule).getRule().endParsing();
        } catch (Exception e) {
          throw new RuntimeException("Unable to call endParsing() method on the following rule '" + grammar.getClass().getName() + "."
              + field.getName() + "'", e);
        }
      }
    }
  }

}
