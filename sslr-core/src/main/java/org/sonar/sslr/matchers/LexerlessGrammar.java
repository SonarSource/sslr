/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.matchers;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;
import org.sonar.sslr.internal.matchers.GrammarException;

import java.lang.reflect.Field;

// TODO Godin: extends class Grammar in order to ease migration (e.g. AST visitors),
// but maybe would be better to have a way to distinguish lexerless grammar from previous
public abstract class LexerlessGrammar extends Grammar {

  @Override
  protected void instanciateRuleFields() {
    for (Field ruleField : getAllRuleFields(this.getClass())) {
      String ruleName = ruleField.getName();
      try {
        Rule rule = new GrammarElementMatcher(ruleName);
        ruleField.setAccessible(true);
        ruleField.set(this, rule);
      } catch (Exception e) {
        throw new GrammarException(e, "Unable to instanciate the rule '" + ruleName + "'");
      }
    }
  }

}
