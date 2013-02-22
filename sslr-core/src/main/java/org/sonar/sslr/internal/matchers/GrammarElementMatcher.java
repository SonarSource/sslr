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
package org.sonar.sslr.internal.matchers;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeSkippingPolicy;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.NeverSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;
import org.sonar.sslr.grammar.GrammarException;

public class GrammarElementMatcher implements Rule, Matcher, AstNodeSkippingPolicy {

  private final String name;
  private final AstNodeType astNodeType;
  private Matcher[] subMatchers;
  private AstNodeSkippingPolicy astNodeSkippingPolicy = new NeverSkipFromAst();

  public GrammarElementMatcher(String name) {
    this.name = name;
    this.astNodeType = this;
  }

  /**
   * @since 1.18
   */
  public GrammarElementMatcher(String name, AstNodeType astNodeType) {
    this.name = name;
    this.astNodeType = astNodeType;
  }

  public GrammarElementMatcher is(Object... elements) {
    throw new AssertionError("Expected that this method not used directly and overridden in MutableParsingRule");
  }

  public GrammarElementMatcher override(Object... elements) {
    throw new AssertionError("Expected that this method not used directly and overridden in MutableParsingRule");
  }

  public void mock() {
    throw new AssertionError("Expected that this method not used directly and overridden in MutableParsingRule");
  }

  public String getName() {
    return name;
  }

  public boolean match(MatcherContext context) {
    if (subMatchers == null) {
      throw new GrammarException("The rule '" + name + "' hasn't beed defined.");
    }
    if (context.getMatchHandler().match(context)) {
      return true;
    }
    for (Matcher subMatcher : subMatchers) {
      if (!context.getSubContext(subMatcher).runMatcher()) {
        context.getMatchHandler().onMissmatch(context);
        return false;
      }
    }
    context.createNode();
    context.getMatchHandler().onMatch(context);
    return true;
  }

  public void skip() {
    astNodeSkippingPolicy = new AlwaysSkipFromAst();
  }

  public void skipIfOneChild() {
    astNodeSkippingPolicy = new SkipFromAstIfOnlyOneChild();
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

  /**
   * @since 1.18
   */
  public AstNodeType getRealAstNodeType() {
    return astNodeType;
  }

}
