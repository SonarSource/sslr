/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.TokenType;

/**
 * This class provides all static matchers to build and extended BNF grammar
 */
public class Matchers {

  private Matchers() {
  };

  /**
   * Syntactic predicate to check that the next tokens don't match an element.
   */
  public static Matcher not(Object element) {
    return new MemoizerMatcher(new NotMatcher(Matcher.convertToMatcher(element)));
  }

  /**
   * Match the element if and only if the first token of this element is adjacent to the previous consumed token.
   * 
   * <pre>
   * {@code 
   * >------previous_element---- element ---->
   * }
   * </pre>
   * 
   * Without any space between previous_element and element
   */
  public static Matcher adjacent(Object element) {
    return new MemoizerMatcher(new AdjacentMatcher(Matcher.convertToMatcher(element)));
  }

  /**
   * Optionally match the element(s).
   * 
   * <pre>
   * {@code 
   * >------element 1----element 2----element n---->
   *    |                                       |
   *     ---------------------------------------
   * }
   * </pre>
   */
  public static Matcher opt(Object... elements) {
    if (elements.length == 1) {
      return new MemoizerMatcher(new OptMatcher(Matcher.convertToMatcher(elements[0])));
    }
    return new MemoizerMatcher(new OptMatcher(new AndMatcher(Matcher.convertToMatchers(elements))));
  }

  /**
   * Syntactic predicate to check that the next tokens match some elements.
   */
  public static Matcher next(Object... elements) {
    return new MemoizerMatcher(new NextMatcher(new AndMatcher(Matcher.convertToMatchers(elements))));
  }

  /**
   * Match any alternative within the elements exactly once
   * 
   * <pre>
   * {@code 
   * >------element 1----->
   *    |               | 
   *    ----element 2---
   *    |               | 
   *    ----   ...   ---
   *    |               | 
   *    ----element n---
   * }
   * </pre>
   */
  public static Matcher or(Object... elements) {
    return new MemoizerMatcher(new OrMatcher(Matcher.convertToMatchers(elements)));
  }

  /**
   * Consume the next token if and only if the element doesn't match
   */
  public static Matcher anyTokenButNot(Object element) {
    return new MemoizerMatcher(new AnyTokenButNotMatcher(Matcher.convertToMatcher(element)));
  }

  /**
   * Match the next token if and only if its type belongs to the provided list
   */
  public static Matcher isOneOfThem(TokenType... types) {
    return new MemoizerMatcher(new TokenTypesMatcher(types));
  }

  /**
   * Match all elements in a sequential order.
   * 
   * <pre>
   * {@code 
   * >------element 1----element 2---- ... ----element n---->
   * }
   * </pre>
   */
  public static Matcher and(Object... elements) {
    if (elements.length == 1) {
      return new MemoizerMatcher(Matcher.convertToMatcher(elements[0]));
    }
    return new MemoizerMatcher(new AndMatcher(Matcher.convertToMatchers(elements)));
  }

  /**
   * Consume all tokens between token from and token to.
   * 
   * <pre>
   * {@code 
   * >------ from ---- ... ---- to ---->
   * }
   * </pre>
   */
  public static Matcher bridge(TokenType from, TokenType to) {
    return new MemoizerMatcher(new BridgeMatcher(from, to));
  }

  /**
   * For unit test only Consume the next token whatever it is
   */
  public static Matcher isTrue() {
    return new MemoizerMatcher(new BooleanMatcher(true));
  }

  /**
   * For unit test only Not consume the next token whatever it is
   */
  public static Matcher isFalse() {
    return new MemoizerMatcher(new BooleanMatcher(false));
  }

  /**
   * Consume the next token whatever it is
   */
  public static Matcher anyToken() {
    return new MemoizerMatcher(new AnyTokenMatcher());
  }

  /**
   * Consume all tokens as long as the element is not encountered. The first token of the element is also consumed.
   * 
   * <pre>
   * {@code 
   * >------ ... ---- element ---->
   * }
   * </pre>
   */
  public static Matcher till(Object element) {
    return new MemoizerMatcher(new InclusiveTillMatcher(Matcher.convertToMatcher(element)));
  }

  /**
   * Consume all tokens as long one of the provided elements is not encountered.
   * 
   * <pre>
   * {@code 
   * >------ ... ---- element 1 ---->
   *              |-- element 2 --|
   *              |--    ...    --|
   *              |-- element n --|
   * }
   * </pre>
   */
  public static Matcher exclusiveTill(Object... elements) {
    return new MemoizerMatcher(new ExclusiveTillMatcher(Matcher.convertToMatchers(elements)));
  }

  /**
   * Consume the next token if and only if its type and its value match the provided ones.
   */
  public static Matcher token(TokenType type, String value) {
    return new MemoizerMatcher(new TokenTypeAndValueMatcher(type, value));
  }

  /**
   * Optionally match each element in sequential order but at least one must match.
   * 
   * <pre>
   * {@code 
   * >------element 1-------element 2--------element n------>
   *    |             |   |            |   |            |
   *     -------------     ------------     ------------
   * }
   * </pre>
   */
  public static Matcher atLeastOne(Object... elements) {
    return new MemoizerMatcher(new AtLeastOneMatcher(Matcher.convertToMatchers(elements)));
  }

  /**
   * Match the longest alternative within the elements exactly once
   * 
   * <pre>
   * {@code 
   * >------element 1----->
   *    |               | 
   *    ----element 2---
   *    |               | 
   *    ----   ...   ---
   *    |               | 
   *    ----element n---
   * }
   * </pre>
   */
  public static Matcher longestOne(Object... elements) {
    return new MemoizerMatcher(new LongestOneMatcher(Matcher.convertToMatchers(elements)));
  }

  /**
   * Match elements sequence zero or more times
   * 
   * <pre>
   * {@code 
   *      -----<----------------<------------------<------ 
   *     |                                                |  
   * >------element 1----element 2---- ... ----element n---------->
   *  |                                                       |  
   *   ------------------------->----------------------------- 
   * }
   * </pre>
   */
  public static Matcher o2n(Object... objects) {
    Matcher[] matchers = Matcher.convertToMatchers(objects);
    if (matchers.length == 0) {
      throw new IllegalStateException("You must define at least one matcher.");
    } else {
      return new MemoizerMatcher(new ZeroToNMatcher(new AndMatcher(matchers)));
    }
  }

  /**
   * Match elements sequence one or more times
   * 
   * <pre>
   * {@code 
   *     ------<----------------<------------------<------ 
   *    |                                                 |  
   * >------element 1----element 2---- ... ----element n------>
   * }
   * </pre>
   */
  public static Matcher one2n(Object... elements) {
    Matcher[] matchers = Matcher.convertToMatchers(elements);
    if (matchers.length == 0) {
      throw new IllegalStateException("You must define at least one matcher.");
    } else {
      return new MemoizerMatcher(new OneToNMatcher(new AndMatcher(matchers)));
    }
  }
}
