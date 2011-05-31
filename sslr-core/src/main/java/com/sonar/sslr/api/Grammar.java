/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.api;

import java.lang.reflect.Field;

import com.sonar.sslr.impl.matcher.RuleBuilder;

/**
 * A Grammar must be implemented to define the syntactic rules of a language.
 * 
 * @see Rule
 * @see <a href="http://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form">Backus–Naur Form</a>
 */
public abstract class Grammar {

  public Grammar() {
    instanciateRuleFields();
  }

  private void instanciateRuleFields() {
    Field[] fields = this.getClass().getFields();
    for (Field field : fields) {
      String fieldName = field.getName();
      try {
        if (field.getType() == LeftRecursiveRule.class) {
          field.set(this, RuleBuilder.newLeftRecursiveRuleBuilder(fieldName));
        } else if (field.getType() == Rule.class) {
          field.set(this, RuleBuilder.newRuleBuilder(fieldName));
        }
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
}
