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

import com.sonar.sslr.api.TokenType;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.Instruction;
import org.sonar.sslr.internal.vm.lexerful.TokenTypesExpression;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 */
public final class TokenTypesMatcher extends TokenMatcher {

  private final Set<TokenType> tokenTypes = new HashSet<TokenType>();

  public TokenTypesMatcher(TokenType... types) {
    super(false);
    for (TokenType keyword : types) {
      this.tokenTypes.add(keyword);
    }
  }

  @Override
  public String toString() {
    return "isOneOfThem";
  }

  public Instruction[] compile(CompilationHandler compiler) {
    return new TokenTypesExpression(tokenTypes.toArray(new TokenType[tokenTypes.size()])).compile(compiler);
  }

}
