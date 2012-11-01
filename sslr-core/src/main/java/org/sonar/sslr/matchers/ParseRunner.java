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
package org.sonar.sslr.matchers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.sonar.sslr.api.Rule;
import org.sonar.sslr.internal.matchers.*;

import java.util.List;

/**
 * <p>This class is not intended to be subclassed by clients.</p>
 *
 * @since 2.0
 */
public class ParseRunner {

  private final Matcher rootMatcher;

  public ParseRunner(Rule rule) {
    this.rootMatcher = (Matcher) Preconditions.checkNotNull(rule, "rule");
  }

  public ParsingResult parse(char[] input) {
    InputBuffer inputBuffer = new ImmutableInputBuffer(input);
    Memoizer memoizer = new Memoizer(input.length);
    ErrorLocatingHandler errorLocatingHandler = new ErrorLocatingHandler(memoizer);
    MatcherContext matcherContext = new BasicMatcherContext(inputBuffer, errorLocatingHandler, rootMatcher);
    boolean matched = matcherContext.runMatcher();
    if (matched) {
      return new ParsingResult(inputBuffer, matched, matcherContext.getNode(), null);
    } else {
      // Perform second run in order to collect information for error report

      // TODO Godin: Looks like memoized nodes should be removed for correct error reporting,
      // but maybe we can remove only some of them
      memoizer = new Memoizer(input.length);
      ErrorReportingHandler errorReportingHandler = new ErrorReportingHandler(memoizer, errorLocatingHandler.getErrorIndex());
      matched = new BasicMatcherContext(inputBuffer, errorReportingHandler, rootMatcher).runMatcher();
      // failure should be permanent, otherwise something generally wrong
      Preconditions.checkState(!matched);

      StringBuilder sb = new StringBuilder("failed to match");
      if (errorReportingHandler.getFailedPaths().size() > 1) {
        sb.append(" none of");
      }
      sb.append(':');
      for (List<MatcherPathElement> failedPath : errorReportingHandler.getFailedPaths()) {
        Matcher failedMatcher = Iterables.getLast(failedPath).getMatcher();
        sb.append(' ').append(((GrammarElementMatcher) failedMatcher).getName());
      }
      ParseError parseError = new ParseError(inputBuffer, errorLocatingHandler.getErrorIndex(), sb.toString(), errorReportingHandler.getFailedPaths());
      return new ParsingResult(inputBuffer, matched, null, parseError);
    }
  }

}
