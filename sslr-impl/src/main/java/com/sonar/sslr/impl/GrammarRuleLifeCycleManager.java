/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl;

import java.lang.reflect.Field;

import org.apache.commons.lang.ClassUtils;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.LeftRecursiveRuleImpl;
import com.sonar.sslr.impl.matcher.RuleImpl;

/**
 * Utility class for handling grammar rule life cycle
 */
public final class GrammarRuleLifeCycleManager {

  private GrammarRuleLifeCycleManager() {
  }

  /**
   * Initializes the given grammar with standard rules.
   * 
   * @param rules
   *          the grammar object to initialize
   * @param grammar
   *          the class that represents this grammar
   */
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

  /**
   * Initializes the given grammar with rules that support left recursion.
   * 
   * @param rules
   *          the grammar object to initialize
   * @param grammar
   *          the class that represents this grammar
   */
  public static void initializeLeftRecursionRuleFields(Object rules, Class<?> grammar) {
    Field[] fields = grammar.getDeclaredFields();
    for (Field field : fields) {
      if (field.getType() == Rule.class) {
        String fieldName = field.getName();
        try {
          field.set(rules, new LeftRecursiveRuleImpl(fieldName));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Reinitializes the state of all the left recursion rules of the given grammar.
   * 
   * @param rules
   *          the grammar object that contains the rules to reinit
   */
  public static void reinitializeLeftRecursionRuleFields(Object rules) {
    Field[] fields = rules.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (field.getType() == Rule.class) {
        try {
          Object rule = field.get(rules);
          if (ClassUtils.isAssignable(rule.getClass(), LeftRecursiveRuleImpl.class)) {
            ((LeftRecursiveRuleImpl) rule).reInitState();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

}
