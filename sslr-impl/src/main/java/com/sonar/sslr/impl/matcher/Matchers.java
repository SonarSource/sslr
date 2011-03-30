/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.TokenType;

public class Matchers {

  private static boolean strictOrMode = false;

  private Matchers() {
  };

  public static void activeStrictOrMode() {
    strictOrMode = true;
  }

  public static void deactivateStrictOrMode() {
    strictOrMode = false;
  }

  public static Matcher opt(Object object) {
    return new MemoizerMatcher(new OpMatcher(Matcher.convertToMatcher(object)));
  }

  /**
   * Be careful this matcher doesn't consume any Token. See 'anyTokenButNot(Object object)' if you want to consume next token.
   * 
   * @param object
   * @return
   */
  public static Matcher not(Object object) {
    return new MemoizerMatcher(new NotMatcher(Matcher.convertToMatcher(object)));
  }

  public static Matcher adjacent(Object object) {
    return new MemoizerMatcher(new AdjacentMatcher(Matcher.convertToMatcher(object)));
  }

  public static Matcher adjacent(Object... objects) {
    return new MemoizerMatcher(new AdjacentMatcher(new AndMatcher(Matcher.convertToMatchers(objects))));
  }

  public static Matcher opt(Object... objects) {
    return new MemoizerMatcher(new OpMatcher(new AndMatcher(Matcher.convertToMatchers(objects))));
  }

  public static Matcher next(Object... objects) {
    return new MemoizerMatcher(new NextMatcher(new AndMatcher(Matcher.convertToMatchers(objects))));
  }

  public static Matcher or(Object... matchers) {
    if (strictOrMode) {
      return new MemoizerMatcher(new StrictOrMatcher(Matcher.convertToMatchers(matchers)));
    } else {
      return new MemoizerMatcher(new OrMatcher(Matcher.convertToMatchers(matchers)));
    }
  }

  public static Matcher anyTokenButNot(Object object) {
    return new MemoizerMatcher(new AnyTokenButNotMatcher(Matcher.convertToMatcher(object)));
  }

  public static Matcher isOneOfThem(TokenType... keywords) {
    return new MemoizerMatcher(new TokenTypesMatcher(keywords));
  }

  public static Matcher and(Object... matchers) {
    if (matchers.length == 1) {
      return new MemoizerMatcher(Matcher.convertToMatcher(matchers[0]));
    }
    return new MemoizerMatcher(new AndMatcher(Matcher.convertToMatchers(matchers)));
  }

  public static Matcher bridge(TokenType from, TokenType to) {
    return new MemoizerMatcher(new BridgeMatcher(from, to));
  }

  public static Matcher isTrue() {
    return new MemoizerMatcher(new BooleanMatcher(true));
  }

  public static Matcher anyToken() {
    return new MemoizerMatcher(new AnyTokenMatcher());
  }

  public static Matcher isFalse() {
    return new MemoizerMatcher(new BooleanMatcher(false));
  }

  public static Matcher till(Object matcher) {
    return new MemoizerMatcher(new InclusiveTillMatcher(Matcher.convertToMatcher(matcher)));
  }

  public static Matcher exclusiveTill(Object... matchers) {
    return new MemoizerMatcher(new ExclusiveTillMatcher(Matcher.convertToMatchers(matchers)));
  }
  
  public static Matcher token(TokenType type, String value) {
  	return new MemoizerMatcher(new TokenTypeAndValueMatcher(type, value));
  }

  public static Matcher o2n(Object... objects) {
    Matcher[] matchers = Matcher.convertToMatchers(objects);
    if (matchers.length == 0) {
      throw new IllegalStateException("You must define at least one matcher.");
    } else {
      return new MemoizerMatcher(new ZeroToNMatcher(new AndMatcher(matchers)));
    }
  }

  public static Matcher one2n(Object... objects) {
    Matcher[] matchers = Matcher.convertToMatchers(objects);
    if (matchers.length == 0) {
      throw new IllegalStateException("You must define at least one matcher.");
    } else {
      return new MemoizerMatcher(new OneToNMatcher(new AndMatcher(matchers)));
    }
  }
}
