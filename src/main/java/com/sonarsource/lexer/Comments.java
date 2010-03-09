/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonarsource.lexer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Comments implements Iterable<Token> {

  private Map<Integer, Token> comments = new HashMap<Integer, Token>();

  public Comments(List<Token> comments) {
    for (Token comment : comments) {
      this.comments.put(comment.getLine(), comment);
    }
  }

  public Iterator<Token> iterator() {
    return comments.values().iterator();
  }

  public boolean isThereCommentAtLine(int line) {
    return comments.containsKey(line);
  }

  public Token getCommentAtLine(int line) {
    return comments.get(line);
  }

}
