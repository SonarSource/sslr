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
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.NextNotExpression;
import org.sonar.sslr.internal.vm.SequenceExpression;
import org.sonar.sslr.internal.vm.ZeroOrMoreExpression;
import org.sonar.sslr.internal.vm.lexerful.AnyTokenExpression;

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public final class InclusiveTillMatcher extends StatelessMatcher {

  public InclusiveTillMatcher(Matcher matcher) {
    super(matcher);
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    AstNode astNode = new AstNode(null, "till", parsingState.peekTokenIfExists(parsingState.lexerIndex, this));

    while (!super.children[0].isMatching(parsingState)) {
      Token token = parsingState.popToken(this);
      astNode.addChild(new AstNode(token));
    }

    astNode.addChild(super.children[0].match(parsingState));
    return astNode;
  }

  @Override
  public String toString() {
    return "till";
  }

  public Instruction[] compile(CompilationHandler compiler) {
    return new SequenceExpression(
        new ZeroOrMoreExpression(
            new SequenceExpression(
                new NextNotExpression(children[0]),
                AnyTokenExpression.INSTANCE)),
        children[0]).compile(compiler);
  }

}
