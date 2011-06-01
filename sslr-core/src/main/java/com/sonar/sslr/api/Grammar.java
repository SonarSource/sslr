/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.sonar.sslr.impl.matcher.RuleDefinition;

/**
 * A Grammar is used to list and define all production rules (@link {@link Rule}) of a context-free grammar. For each production rule, a
 * public Rule field must exist. All those public Rule fields are automatically instantiated when creating a Grammar object.
 * 
 * @see Rule
 * @see <a href="http://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form">Backus–Naur Form</a>
 */
public abstract class Grammar {

  private Map<String, Rule> ruleIndex = new HashMap<String, Rule>();

  public Grammar() {
    instanciateRuleFields();
  }

  private void instanciateRuleFields() {
    Field[] fields = this.getClass().getFields();
    for (Field field : fields) {
      String fieldName = field.getName();
      try {
        Rule rule;
        if (field.getType() == LeftRecursiveRule.class) {
          rule = RuleDefinition.newLeftRecursiveRuleBuilder(fieldName);
        } else if (field.getType() == Rule.class) {
          rule = RuleDefinition.newRuleBuilder(fieldName);
        } else {
          continue;
        }
        field.set(this, rule);
        ruleIndex.put(fieldName, rule);
      } catch (Exception e) {
        throw new RuntimeException("Unable to instanciate the rule '" + this.getClass().getName() + "." + fieldName + "'", e);
      }
    }
  }

  /**
   * Each Grammar has always an entry point whose name is usually by convention the "Computation Unit".
   * 
   * @return the entry point of this Grammar
   */
  public abstract Rule getRootRule();

  /**
   * Utility method to retrieve the instance of a Rule according to its name.
   * 
   * @param ruleName
   *          the rule name
   * @return the Rule object if exists, otherwise a null value is returned
   */
  public Rule findRuleByName(String ruleName) {
    return ruleIndex.get(ruleName);
  }
}
