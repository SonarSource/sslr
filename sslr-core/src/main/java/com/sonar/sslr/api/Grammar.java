/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.sonar.sslr.impl.matcher.RuleDefinition;

/**
 * A Grammar is used to list and define all production rules (@link {@link Rule}) of a context-free grammar. For each production rule, a
 * public Rule field must exist. All those public Rule fields are automatically instantiated when creating a Grammar object.
 * 
 * @see Rule
 * @see <a href="http://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form">Backus�Naur Form</a>
 */
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
  public static Field[] getRuleFields(Class grammarClass) {
    Field[] fields = grammarClass.getDeclaredFields();

    List<Field> ruleFields = new ArrayList<Field>();
    for (Field field : fields) {
      if (Rule.class.isAssignableFrom(field.getType())) {
        ruleFields.add(field);
      }
    }

    return ruleFields.toArray(new Field[ruleFields.size()]);
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
  public static Field[] getAllRuleFields(Class grammarClass) {
    Field[] ruleFields = getRuleFields(grammarClass);

    Class superClass = grammarClass.getSuperclass();
    while (superClass != null) {
      ruleFields = (Field[]) ArrayUtils.addAll(ruleFields, getRuleFields(superClass));
      superClass = superClass.getSuperclass();
    }

    return ruleFields;
  }

  private void instanciateRuleFields() {
    for (Field ruleField : getAllRuleFields(this.getClass())) {
      String ruleName = ruleField.getName();
      try {
        Rule rule;
        if (ruleField.getType() == LeftRecursiveRule.class) {
          rule = RuleDefinition.newLeftRecursiveRuleBuilder(ruleName);
        } else if (ruleField.getType() == Rule.class) {
          rule = RuleDefinition.newRuleBuilder(ruleName);
        } else {
          throw new IllegalArgumentException("A rule must be either a Rule or a LeftRecursiveRule.");
        }
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
