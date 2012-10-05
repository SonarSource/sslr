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
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.ast.AlwaysSkipFromAst;
import com.sonar.sslr.impl.ast.NeverSkipFromAst;
import com.sonar.sslr.impl.ast.SkipFromAstIfOnlyOneChild;
import org.sonar.sslr.matchers.Matchers;

// TODO Godin: Retrofit methods with varargs (SSLR-215)
public class GrammarElementMatcher implements Rule, Matcher, AstNodeSkippingPolicy {

  private final String name;
  private Matcher subMatcher;
  private TokenType tokenType;
  private AstNodeSkippingPolicy astNodeSkippingPolicy = new NeverSkipFromAst();

  public GrammarElementMatcher(String name) {
    this.name = name;
  }

  public GrammarElementMatcher is(Object... elements) {
    if (subMatcher != null) {
      throw new IllegalStateException("The rule '" + name + "' has already been defined somewhere in the grammar.");
    }
    setSubMatcher(elements);
    return this;
  }

  public GrammarElementMatcher override(Object... elements) {
    setSubMatcher(elements);
    return this;
  }

  public void mock() {
    setSubMatcher(getName(), Matchers.firstOf(Matchers.regexp("\\s++"), Matchers.endOfInput()));
  }

  private void setSubMatcher(Object... elements) {
    subMatcher = Matchers.sequence(elements);
  }

  public String getName() {
    return name;
  }

  public boolean match(MatcherContext context) {
    return context.getSubContext(subMatcher).runMatcher();
  }

  public GrammarElementMatcher setTokenType(TokenType tokenType) {
    this.tokenType = tokenType;
    return this;
  }

  public TokenType getTokenType() {
    return tokenType;
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

}
