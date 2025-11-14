/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.internal.grammar;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.NeverSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;
import org.sonar.sslr.grammar.GrammarException;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.internal.matchers.Matcher;
import org.sonar.sslr.internal.vm.CompilableGrammarRule;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.EndOfInputExpression;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.MemoParsingExpression;
import org.sonar.sslr.internal.vm.ParsingExpression;
import org.sonar.sslr.internal.vm.PatternExpression;
import org.sonar.sslr.internal.vm.RuleRefExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.StringExpression;
import org.sonar.sslr.parser.GrammarOperators;

public class MutableParsingRule implements CompilableGrammarRule, Matcher, Rule, AstNodeSkippingPolicy, MemoParsingExpression, GrammarRuleKey {

  private final GrammarRuleKey ruleKey;
  private final String name;
  private ParsingExpression expression;
  private AstNodeSkippingPolicy astNodeSkippingPolicy = NeverSkipFromAst.INSTANCE;

  public MutableParsingRule(String name) {
    this.ruleKey = this;
    this.name = name;
  }

  public MutableParsingRule(GrammarRuleKey ruleKey) {
    this.ruleKey = ruleKey;
    this.name = ruleKey.toString();
  }

  public String getName() {
    return name;
  }

  public AstNodeType getRealAstNodeType() {
    return ruleKey;
  }

  @Override
  public GrammarRuleKey getRuleKey() {
    return ruleKey;
  }

  @Override
  public ParsingExpression getExpression() {
    return expression;
  }

  @Override
  public Rule is(Object... e) {
    if (expression != null) {
      throw new GrammarException("The rule '" + ruleKey + "' has already been defined somewhere in the grammar.");
    }
    setExpression((ParsingExpression) GrammarOperators.sequence(e));
    return this;
  }

  @Override
  public Rule override(Object... e) {
    setExpression((ParsingExpression) GrammarOperators.sequence(e));
    return this;
  }

  @Override
  public void mock() {
    setExpression(new SequenceExpression(
        new StringExpression(getName()),
        new FirstOfExpression(
            new PatternExpression("\\s++"),
            EndOfInputExpression.INSTANCE)));
  }

  @Override
  public void setExpression(ParsingExpression expression) {
    this.expression = expression;
  }

  @Override
  public void skip() {
    astNodeSkippingPolicy = AlwaysSkipFromAst.INSTANCE;
  }

  @Override
  public void skipIfOneChild() {
    astNodeSkippingPolicy = SkipFromAstIfOnlyOneChild.INSTANCE;
  }

  @Override
  public boolean hasToBeSkippedFromAst(AstNode node) {
    return astNodeSkippingPolicy.hasToBeSkippedFromAst(node);
  }

  @Override
  public Instruction[] compile(CompilationHandler compiler) {
    return compiler.compile(new RuleRefExpression(ruleKey));
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean shouldMemoize() {
    return true;
  }

}
