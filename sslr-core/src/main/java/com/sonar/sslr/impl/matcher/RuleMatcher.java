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
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.RecognitionException;
import com.sonar.sslr.impl.ParsingState;

public final class RuleMatcher extends MemoizedMatcher {

  private final String name;
  private boolean recoveryRule = false;
  private AstNodeType astNodeType;

  public RuleMatcher(String name) {
    this.name = name;
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    int startIndex = parsingState.lexerIndex;
    if (super.children.length == 0) {
      throw new IllegalStateException("The rule '" + name + "' hasn't beed defined.");
    }

    if (recoveryRule) {
      RecognitionException recognitionException = parsingState.extendedStackTrace == null ?
          new RecognitionException(parsingState, false) : new RecognitionException(parsingState.extendedStackTrace, false);

      if (super.children[0].isMatching(parsingState)) {
        parsingState.notifyListeners(recognitionException);
      }
    }

    AstNode childNode = super.children[0].match(parsingState);

    AstNode astNode = new AstNode(astNodeType, name, parsingState.peekTokenIfExists(startIndex, super.children[0]));
    astNode.addChild(childNode);
    return astNode;
  }

  public void setNodeType(AstNodeType astNodeType) {
    this.astNodeType = astNodeType;
  }

  public String getName() {
    return name;
  }

  public void recoveryRule() {
    recoveryRule = true;
  }

  @Override
  public String toString() {
    return getName();
  }

}
