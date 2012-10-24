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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.Rule;
import org.sonar.sslr.internal.matchers.*;

import java.util.List;

/**
 * <p>This class is not intended to be subclassed by clients.</p>
 */
public class ParseRunner {

  private final Matcher rootMatcher;

  public ParseRunner(Rule rule) {
    this.rootMatcher = (Matcher) rule;
  }

  public ParsingResult parse(char[] input) {
    Memoizer memoizer = new Memoizer(input.length);
    ErrorLocatingHandler errorLocatingHandler = new ErrorLocatingHandler(memoizer);
    MatcherContext matcherContext = new BasicMatcherContext(input, errorLocatingHandler, rootMatcher);
    boolean matched = matcherContext.runMatcher();
    if (matched) {
      return new ParsingResult(matched, matcherContext.getNode(), null);
    } else {
      // Perform second run in order to collect information for error report

      // TODO Godin: Looks like memoized nodes should be removed for correct error reporting,
      // but maybe we can remove only some of them
      memoizer = new Memoizer(input.length);
      ErrorReportingHandler errorReportingHandler = new ErrorReportingHandler(memoizer, errorLocatingHandler.errorIndex);
      new BasicMatcherContext(input, errorReportingHandler, rootMatcher).runMatcher();

      StringBuilder sb = new StringBuilder("expected");
      if (errorReportingHandler.failedPaths.size() > 1) {
        sb.append(" one of");
      }
      sb.append(':');
      for (List<MatcherPathElement> failedPath : errorReportingHandler.failedPaths) {
        Matcher failedMatcher = Iterables.getLast(failedPath).getMatcher();
        sb.append(' ').append(((GrammarElementMatcher) failedMatcher).getName());
      }
      ParseError parseError = new ParseError(new InputBuffer(input), errorReportingHandler.errorIndex, sb.toString(), errorReportingHandler.failedPaths);
      return new ParsingResult(matched, null, parseError);
    }
  }

  private static class ErrorReportingHandler implements MatchHandler {

    private final MatchHandler delegate;
    private final int errorIndex;
    private final List<List<MatcherPathElement>> failedPaths = Lists.newArrayList();

    public ErrorReportingHandler(MatchHandler delegate, int errorIndex) {
      this.delegate = delegate;
      this.errorIndex = errorIndex;
    }

    public boolean match(MatcherContext context) {
      return delegate.match(context);
    }

    public void onMatch(MatcherContext context) {
      delegate.onMatch(context);
    }

    public void onMissmatch(MatcherContext context) {
      // We are interested in errors, which occur only on terminals:
      if (errorIndex == context.getCurrentIndex() && isTerminal(context.getMatcher())) {
        failedPaths.add(getPath((BasicMatcherContext) context));
      }
    }

    private static boolean isTerminal(Matcher matcher) {
      return ((GrammarElementMatcher) matcher).getTokenType() != null;
    }

    private static List<MatcherPathElement> getPath(BasicMatcherContext context) {
      List<MatcherPathElement> list = Lists.newArrayList();
      int endIndex = context.getCurrentIndex();
      while (context != null) {
        if (context.getMatcher() instanceof GrammarElementMatcher) {
          list.add(new MatcherPathElement(context.getMatcher(), context.getStartIndex(), endIndex));
          endIndex = context.getStartIndex();
        }
        context = context.getParent();
      }
      return ImmutableList.copyOf(Iterables.reverse(list));
    }

  }

  private static class ErrorLocatingHandler implements MatchHandler {

    private final MatchHandler delegate;
    private int errorIndex = -1;

    public ErrorLocatingHandler(MatchHandler delegate) {
      this.delegate = delegate;
    }

    public boolean match(MatcherContext context) {
      return delegate.match(context);
    }

    public void onMatch(MatcherContext context) {
      delegate.onMatch(context);
    }

    public void onMissmatch(MatcherContext context) {
      // We are interested in errors, which occur only on terminals:
      // FIXME Godin: for the moment we assume that error cannot occur inside of predicate or inside of terminal
      if (errorIndex < context.getCurrentIndex()) {
        errorIndex = context.getCurrentIndex();
      }
    }

  }

}
