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
import com.sonar.sslr.impl.ParsingState;

/**
 * A {@link Matcher} that tries all of its submatchers in sequence and only succeeds if all submatchers succeed.
 */
public final class AndMatcher extends StatelessMatcher {

  protected AndMatcher(Matcher... matchers) {
    super(matchers);
  }

  @Override
  protected AstNode matchWorker(ParsingState parsingState) {
    AstNode[] childNodes = new AstNode[super.children.length];
    int startIndex = parsingState.lexerIndex;

    for (int i = 0; i < super.children.length; i++) {
      childNodes[i] = super.children[i].match(parsingState);
    }

    AstNode astNode = new AstNode(null, "AllMatcher", parsingState.peekTokenIfExists(startIndex, this));
    for (AstNode childNode : childNodes) {
      astNode.addChild(childNode);
    }
    return astNode;
  }

  @Override
  public String toString() {
    return "and";
  }

}
