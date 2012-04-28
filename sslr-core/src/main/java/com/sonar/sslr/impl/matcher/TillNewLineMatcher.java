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

import static com.sonar.sslr.api.GenericTokenType.*;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.ParsingState;

public final class TillNewLineMatcher extends StatelessMatcher {

  protected TillNewLineMatcher() {
    super();
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    int currentLine = parsingState.lexerIndex - 1 >= 0 ? parsingState.readToken(parsingState.lexerIndex - 1).getLine() : 1;

    AstNode astNode = new AstNode(null, "tillNewLine", parsingState.peekTokenIfExists(parsingState.lexerIndex, this));
    for (int i = parsingState.lexerIndex; i < parsingState.lexerSize && parsingState.peekToken(this).getLine() == currentLine
        && parsingState.peekToken(this).getType() != EOF; i++) {
      Token token = parsingState.popToken(this);
      astNode.addChild(new AstNode(token));
    }

    return astNode;
  }

  @Override
  public String toString() {
    return "tillNewLine()";
  }

}
