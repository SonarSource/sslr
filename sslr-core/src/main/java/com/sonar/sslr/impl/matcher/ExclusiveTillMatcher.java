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
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.FirstOfExpression;
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;
import org.sonar.sslr.internal.vm.lexerful.AnyTokenExpression;

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public final class ExclusiveTillMatcher extends StatelessMatcher {

  public ExclusiveTillMatcher(Matcher... matchers) {
    super(matchers);
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    Token nextToken = parsingState.peekTokenIfExists(parsingState.lexerIndex, this);

    AstNode astNode = new AstNode(null, "exclusiveTillMatcher", nextToken);
    while (nothingMatch(parsingState)) {
      Token token = parsingState.popToken(this);
      astNode.addChild(new AstNode(token));
    }

    return astNode;
  }

  private boolean nothingMatch(ParsingState parsingState) {
    for (Matcher matcher : super.children) {
      if (matcher.isMatching(parsingState)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "exclusiveTill";
  }

  public Instruction[] compile(CompilationHandler compiler) {
    return new ZeroOrMoreExpression(
        new SequenceExpression(
            new NextNotExpression(
                // TODO firstOf is useless in case of single sub-expression
                new FirstOfExpression(children)),
            AnyTokenExpression.INSTANCE)).compile(compiler);
  }

}
