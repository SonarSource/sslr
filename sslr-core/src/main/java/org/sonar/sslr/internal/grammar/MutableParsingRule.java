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
package org.sonar.sslr.internal.grammar;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.NeverSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.matchers.GrammarElementMatcher;
import org.sonar.sslr.internal.matchers.MatchersUtils2;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.PatternExpression;
import org.sonar.sslr.internal.vm.RuleRefExpression;

/**
 * TODO Replacement for {@link GrammarElementMatcher}.
 */
public class MutableParsingRule extends GrammarElementMatcher implements Rule, AstNodeSkippingPolicy, ParsingExpression, GrammarRuleKey {

  private static final AstNodeSkippingPolicy NEVER = new NeverSkipFromAst();
  private static final AstNodeSkippingPolicy ALWAYS = new AlwaysSkipFromAst();
  private static final AstNodeSkippingPolicy IF_ONE_CHILD = new SkipFromAstIfOnlyOneChild();

  private final GrammarRuleKey ruleKey;
  private ParsingExpression expression;
  private AstNodeSkippingPolicy astNodeSkippingPolicy = NEVER;

  public MutableParsingRule(String name) {
    super(name);
    this.ruleKey = this;
  }

  public MutableParsingRule(GrammarRuleKey ruleKey) {
    super(ruleKey.toString(), ruleKey);
    this.ruleKey = ruleKey;
  }

  public GrammarRuleKey getRuleKey() {
    return ruleKey;
  }

  public ParsingExpression getExpression() {
    return expression;
  }

  public GrammarElementMatcher is(Object... matchers) {
    if (expression != null) {
      throw new GrammarException("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.");
    }
    setSubMatchers(matchers);
    return this;
  }

  public GrammarElementMatcher override(Object... matchers) {
    setSubMatchers(matchers);
    return this;
  }

  public void mock() {
    setSubMatchers(getName(), new FirstOfExpression(new PatternExpression("\\s++"), EndOfInputExpression.INSTANCE));
  }

  public void setSubMatchers(Object... elements) {
    this.expression = MatchersUtils2.convertToSingleMatcher(elements);
  }

  public void skip() {
    astNodeSkippingPolicy = ALWAYS;
  }

  public void skipIfOneChild() {
    astNodeSkippingPolicy = IF_ONE_CHILD;
  }

  public void skipIf(AstNodeSkippingPolicy policy) {
    astNodeSkippingPolicy = policy;
  }

  public void recoveryRule() {
    throw new UnsupportedOperationException();
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return astNodeSkippingPolicy.hasToBeSkippedFromAst(node);
  }

  public Instruction[] compile(CompilationHandler compiler) {
    return compiler.compile(new RuleRefExpression(ruleKey));
  }

  @Override
  public String toString() {
    return getName();
  }

}
