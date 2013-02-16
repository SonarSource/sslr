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
package org.sonar.sslr.internal.vm;

import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.NeverSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleBuilder;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;

public class ParsingRule implements GrammarRuleBuilder {

  private static final AstNodeSkippingPolicy NEVER = new NeverSkipFromAst();
  private static final AstNodeSkippingPolicy ALWAYS = new AlwaysSkipFromAst();
  private static final AstNodeSkippingPolicy IF_ONE_CHILD = new SkipFromAstIfOnlyOneChild();

  private final VmGrammarBuilder builder;
  private final GrammarRuleKey ruleKey;
  private ParsingExpression expression;
  private AstNodeSkippingPolicy astNodeSkippingPolicy = NEVER;

  public ParsingRule(VmGrammarBuilder builder, GrammarRuleKey ruleKey) {
    this.builder = builder;
    this.ruleKey = ruleKey;
  }

  public ParsingExpression getExpression() {
    return expression;
  }

  public GrammarRuleBuilder is(Object e) {
    if (expression != null) {
      throw new GrammarException("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.");
    }
    expression = builder.convertToExpression(e);
    return this;
  }

  public GrammarRuleBuilder is(Object e, Object... rest) {
    return is(new SequenceExpression(builder.convertToExpressions(Lists.asList(e, rest))));
  }

  public GrammarRuleBuilder override(Object e) {
    expression = builder.convertToExpression(e);
    return this;
  }

  public GrammarRuleBuilder override(Object e, Object... rest) {
    return override(new SequenceExpression(builder.convertToExpressions(Lists.asList(e, rest))));
  }

  public void skip() {
    this.astNodeSkippingPolicy = ALWAYS;
  }

  public void skipIfOneChild() {
    this.astNodeSkippingPolicy = IF_ONE_CHILD;
  }

  public void recoveryRule() {
    throw new UnsupportedOperationException();
  }

  public GrammarElementMatcher convert() {
    // For AstCreator
    GrammarElementMatcher matcher = new GrammarElementMatcher(ruleKey.toString(), ruleKey);
    matcher.skipIf(astNodeSkippingPolicy);
    return matcher;
  }

}
