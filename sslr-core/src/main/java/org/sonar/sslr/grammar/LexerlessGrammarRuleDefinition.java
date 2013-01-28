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
package org.sonar.sslr.grammar;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.Rule;
import org.sonar.sslr.internal.grammar.MatcherBuilder;
import org.sonar.sslr.internal.grammar.MatcherBuilderUtils;

public class LexerlessGrammarRuleDefinition {

  private final GrammarRule rule;
  private MatcherBuilder[] matcherBuilders;

  private enum SkipState {
    DO_NOT_SKIP,
    ALWAYS_SKIP,
    SKIP_IF_ONE_CHILD
  }

  private SkipState skipState;

  public LexerlessGrammarRuleDefinition(GrammarRule rule) {
    this.rule = rule;
    this.matcherBuilders = null;
    this.skipState = SkipState.DO_NOT_SKIP;
  }

  public String getName() {
    return rule.toString();
  }

  public GrammarRule getRule() {
    return rule;
  }

  public LexerlessGrammarRuleDefinition is(Object e1, Object... others) {
    Preconditions.checkState(matcherBuilders == null, "The rule '" + getName() + "' has already been defined somewhere in the grammar.");

    setMatcherBuilders(e1, others);
    return this;
  }

  public LexerlessGrammarRuleDefinition override(Object e1, Object... others) {
    setMatcherBuilders(e1, others);
    return this;
  }

  private void setMatcherBuilders(Object e1, Object... others) {
    Object[] elements = new Object[1 + others.length];
    elements[0] = e1;
    System.arraycopy(others, 0, elements, 1, others.length);
    this.matcherBuilders = MatcherBuilderUtils.lexerlessToMatcherBuilders(elements);
  }

  public void skip() {
    this.skipState = SkipState.ALWAYS_SKIP;
  }

  public void skipIfOneChild() {
    this.skipState = SkipState.SKIP_IF_ONE_CHILD;
  }

  public void build(Grammar g) {
    Preconditions.checkState(matcherBuilders != null, "The rule '" + getName() + "' hasn't beed defined.");

    Rule ruleMatcher = g.rule(rule);
    ruleMatcher.is(MatcherBuilderUtils.build(g, matcherBuilders));

    switch (skipState) {
      case ALWAYS_SKIP:
        ruleMatcher.skip();
        break;
      case SKIP_IF_ONE_CHILD:
        ruleMatcher.skipIfOneChild();
        break;
      default:
        break;
    }
  }

}
