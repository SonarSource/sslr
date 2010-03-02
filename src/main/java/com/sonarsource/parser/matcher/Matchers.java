/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.parser.matcher;

public class Matchers {

  private Matchers() {
  };

  public static Matcher opt(Object object) {
    return new ProxyMatcher(new OpMatcher(Matcher.convertToMatcher(object)));
  }

  public static Matcher opt(Object... objects) {
    return new ProxyMatcher(new OpMatcher(new AndMatcher(Matcher.convertToMatchers(objects))));
  }

  public static Matcher or(Object... matchers) {
    return new ProxyMatcher(new OrMatcher(Matcher.convertToMatchers(matchers)));
  }
  
  public static Matcher strictOr(Object... matchers) {
    return new ProxyMatcher(new StrictOrMatcher(Matcher.convertToMatchers(matchers)));
  }

  public static Matcher not(Object object) {
    return new ProxyMatcher(new NotMatcher(Matcher.convertToMatcher(object)));
  }

  public static Matcher and(Object... matchers) {
    return new ProxyMatcher(new AndMatcher(Matcher.convertToMatchers(matchers)));
  }

  public static Matcher isTrue() {
    return new ProxyMatcher(new BooleanMatcher(true));
  }

  public static Matcher isFalse() {
    return new ProxyMatcher(new BooleanMatcher(false));
  }

  public static Matcher eof() {
    return new ProxyMatcher(new EndOfFileMatcher());
  }

  public static Matcher till(Object matcher) {
    return new ProxyMatcher(new InclusiveTillMatcher(Matcher.convertToMatcher(matcher)));
  }

  public static Matcher exclusiveTill(Object... matchers) {
    return new ProxyMatcher(new ExclusiveTillMatcher(Matcher.convertToMatchers(matchers)));
  }

  public static Matcher eof(Matcher matcher) {
    return new ProxyMatcher(new EndOfFileMatcher(matcher));
  }

  public static ZeroToNMatcher o2n(Object... objects) {
    Matcher[] matchers = Matcher.convertToMatchers(objects);
    if (matchers.length == 0) {
      throw new IllegalStateException("You must define at least one matcher.");
    } else if (matchers.length == 0) {
      return new ZeroToNMatcher(matchers[0]);
    } else {
      return new ZeroToNMatcher(new AndMatcher(matchers));
    }
  }

  public static OneToNMatcher one2n(Object... objects) {
    Matcher[] matchers = Matcher.convertToMatchers(objects);
    if (matchers.length == 0) {
      throw new IllegalStateException("You must define at least one matcher.");
    } else if (matchers.length == 0) {
      return new OneToNMatcher(matchers[0]);
    } else {
      return new OneToNMatcher(new AndMatcher(matchers));
    }
  }
}
