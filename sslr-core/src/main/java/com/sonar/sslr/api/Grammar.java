/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package com.sonar.sslr.api;

import com.sonar.sslr.impl.matcher.RuleDefinition;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.grammar.MutableParsingRule;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Use {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder} to create instances of this class.
 *
 * <p>This class is not intended to be instantiated or subclassed by clients.</p>
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
  public static List<Field> getRuleFields(Class grammarClass) {
    Field[] fields = grammarClass.getDeclaredFields();

    List<Field> ruleFields = new ArrayList<>();
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
        Rule rule;
        if (this instanceof LexerlessGrammar) {
          rule = new MutableParsingRule(ruleName);
        } else {
          rule = new RuleDefinition(ruleName);
        }

        ruleField.setAccessible(true);
        ruleField.set(this, rule);
      } catch (Exception e) {
        throw new GrammarException(e, "Unable to instanciate the rule '" + ruleName + "': " + e.getMessage());
      }
    }
  }

  /**
   * Allows to obtain an instance of grammar rule, which was constructed by
   * {@link org.sonar.sslr.grammar.LexerlessGrammarBuilder} and {@link org.sonar.sslr.grammar.LexerfulGrammarBuilder}.
   *
   * @since 1.18
   */
  public Rule rule(GrammarRuleKey ruleKey) {
    throw new UnsupportedOperationException();
  }

  /**
   * Each Grammar has always an entry point whose name is usually by convention the "Computation Unit".
   *
   * @return the entry point of this Grammar
   */
  public abstract Rule getRootRule();

}
