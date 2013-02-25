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
import com.sonar.sslr.impl.BacktrackingEvent;
import com.sonar.sslr.impl.ParsingState;
import org.sonar.sslr.internal.vm.CompilationHandler;
import org.sonar.sslr.internal.vm.Instruction;

import java.util.Arrays;

/**
 * This class allows to remove usage of {@link BacktrackingEvent} from {@link Matcher}s.
 *
 * <p>This class is not intended to be instantiated or sub-classed by clients.</p>
 *
 * @since 1.14
 */
public class StandardMatcher extends MemoizedMatcher {

  protected StandardMatcher(Matcher... matchers) {
    super(matchers);
  }

  @Override
  public final AstNode match(ParsingState parsingState) {
    // For backward compatibility with other matchers
    MatchResult matchResult = doMatch(parsingState);
    if (matchResult.isMatching()) {
      return matchResult.getAstNode();
    } else {
      throw BacktrackingEvent.create();
    }
  }

  @Override
  protected MatchResult doMatch(ParsingState parsingState) {
    // To be sure that this method implemented for all subclasses
    throw new IllegalStateException("Should be implemented in " + getClass().getName());
  }

  @Override
  protected final AstNode matchWorker(ParsingState parsingState) {
    // To be sure that this method not used in subclasses
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getClass().hashCode();
    result = prime * result + Arrays.hashCode(children);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    StandardMatcher other = (StandardMatcher) obj;
    return Arrays.equals(children, other.children);
  }

  public Instruction[] compile(CompilationHandler compiler) {
    throw new UnsupportedOperationException("This method must be overridden by subclass.");
  }

}
