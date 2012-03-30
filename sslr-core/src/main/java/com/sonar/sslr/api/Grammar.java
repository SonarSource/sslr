/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import com.sonar.sslr.impl.matcher.RuleDefinition;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Grammar {

  public Grammar() {
    instanciateRuleFields();
  }

  /**
   * Find all the direct rule fields declared in the given Grammar class.
   * Inherited rule fields are not returned.
   *
   * @param grammarClass
   *          the class of the Grammar for which rule fields must be found
   * @return the rule fields declared in this class, excluding the inherited ones
   * @see getAllRuleFields
   */
  public static List<Field> getRuleFields(Class grammarClass) {
    Field[] fields = grammarClass.getDeclaredFields();

    List<Field> ruleFields = new ArrayList<Field>();
    for (Field field : fields) {
      if (Rule.class.isAssignableFrom(field.getType())) {
        ruleFields.add(field);
      }
    }

    return ruleFields;
  }

  /**
   * Find all direct and indirect rule fields declared in the given Grammar class.
   * Inherited rule fields are also returned.
   *
   * @param grammarClass
   *          the class of the Grammar for which rule fields must be found
   * @return the rule fields declared in this class, as well as the inherited ones
   * @see getRuleFields
   */
  public static List<Field> getAllRuleFields(Class grammarClass) {
    List<Field> ruleFields = getRuleFields(grammarClass);

    Class superClass = grammarClass.getSuperclass();
    while (superClass != null) {
      ruleFields.addAll(getRuleFields(superClass));
      superClass = superClass.getSuperclass();
    }

    return ruleFields;
  }

  private void instanciateRuleFields() {
    for (Field ruleField : getAllRuleFields(this.getClass())) {
      String ruleName = ruleField.getName();
      try {
        Rule rule = RuleDefinition.newRuleBuilder(ruleName);

        ruleField.setAccessible(true);
        ruleField.set(this, rule);
      } catch (Exception e) {
        throw new RuntimeException("Unable to instanciate the rule '" + ruleName + "'", e);
      }
    }
  }

  /**
   * Each Grammar has always an entry point whose name is usually by convention the "Computation Unit".
   *
   * @return the entry point of this Grammar
   */
  public abstract Rule getRootRule();

}
