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

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all the functions to define a context-free grammar
 */
public final class GrammarFunctions {

  private static final String AT_LEAST_ONE_MATCHER_MESSAGE = "You must define at least one matcher.";

  private static final ThreadLocal<Map<Matcher, Matcher>> MATCHER_CACHE = new ThreadLocal<Map<Matcher, Matcher>>() {

    @Override
    protected Map<Matcher, Matcher> initialValue() {
      return new HashMap<Matcher, Matcher>();
    }
  };

  private static Matcher getCachedMatcher(Matcher matcher) {
    if (MATCHER_CACHE.get().containsKey(matcher)) {
      return MATCHER_CACHE.get().get(matcher);
    }

    MATCHER_CACHE.get().put(matcher, matcher);
    return matcher;
  }

  public static void resetCache() {
    MATCHER_CACHE.set(new HashMap<Matcher, Matcher>());
  }

  private GrammarFunctions() {
  }

  public static final class Standard {

    private Standard() {
    }

    /**
     * Match elements sequence zero or more times
     *
     * <pre>
     * {@code
     *     --------<----------------<------------------<--------
     *    |                                                     |
     * >------element 1----element 2---- ... ----element n---------->
     *    |                                                     |
     *     ------------------------->---------------------------
     * }
     * </pre>
     */
    public static Matcher o2n(Object... objects) {
      return opt(one2n(objects));
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
      return getCachedMatcher(new OneToNMatcher(and(elements)));
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
      return getCachedMatcher(new OptMatcher(and(elements)));
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
      if (elements == null || elements.length == 0) {
        throw new IllegalStateException(AT_LEAST_ONE_MATCHER_MESSAGE);
      } else if (elements.length == 1) {
        return convertToMatcher(elements[0]);
      } else {
        return getCachedMatcher(new OrMatcher(convertToMatchers(elements)));
      }
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
      if (elements == null || elements.length == 0) {
        throw new IllegalStateException(AT_LEAST_ONE_MATCHER_MESSAGE);
      } else if (elements.length == 1) {
        return convertToMatcher(elements[0]);
      } else {
        return getCachedMatcher(new AndMatcher(convertToMatchers(elements)));
      }
    }

  }

  public static final class Predicate {

    private Predicate() {
    }

    /**
     * Syntactic predicate to check that the next tokens don't match an element.
     */
    public static Matcher not(Object element) {
      return getCachedMatcher(new NotMatcher(convertToMatcher(element)));
    }

    /**
     * Syntactic predicate to check that the next tokens match some elements.
     */
    public static Matcher next(Object... elements) {
      return getCachedMatcher(new NextMatcher(Standard.and(elements)));
    }

  }

  public static final class Advanced {

    private Advanced() {
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
      return getCachedMatcher(new AdjacentMatcher(convertToMatcher(element)));
    }

    /**
     * Consume the next token if and only if the element doesn't match
     */
    public static Matcher anyTokenButNot(Object element) {
      return getCachedMatcher(new AnyTokenButNotMatcher(convertToMatcher(element)));
    }

    /**
     * Match the next token if and only if its type belongs to the provided list
     */
    public static Matcher isOneOfThem(TokenType... types) {
      if (types == null || types.length == 0) {
        throw new IllegalStateException(AT_LEAST_ONE_MATCHER_MESSAGE);
      } else {
        return getCachedMatcher(new TokenTypesMatcher(types));
      }
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
      return getCachedMatcher(new BridgeMatcher(from, to));
    }

    /**
     * For unit test only Consume the next token whatever it is
     */
    public static Matcher isTrue() {
      return getCachedMatcher(new BooleanMatcher(true));
    }

    /**
     * For unit test only Not consume the next token whatever it is
     */
    public static Matcher isFalse() {
      return getCachedMatcher(new BooleanMatcher(false));
    }

    /**
     * Consume the next token whatever it is
     */
    public static Matcher anyToken() {
      return getCachedMatcher(new AnyTokenMatcher());
    }

    /**
     * Consume every following token which are on the current line
     */
    public static Matcher tillNewLine() {
      return getCachedMatcher(new TillNewLineMatcher());
    }

    /**
     * Consume all tokens as long as the element is not encountered. The element is also consumed.
     *
     * <pre>
     * {@code
     * >------ ... ---- element ---->
     * }
     * </pre>
     */
    public static Matcher till(Object element) {
      return getCachedMatcher(new InclusiveTillMatcher(convertToMatcher(element)));
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
      return getCachedMatcher(new ExclusiveTillMatcher(convertToMatchers(elements)));
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
      return getCachedMatcher(new LongestOneMatcher(convertToMatchers(elements)));
    }

    /**
     * @since 1.14
     */
    public static Matcher memoizeMatches(Object element) {
      Matcher matcher = convertToMatcher(element);
      return matcher instanceof MemoMatcher ? matcher : getCachedMatcher(new MemoMatcher(matcher));
    }

  }

  protected static Matcher[] convertToMatchers(Object[] objects) {
    if (objects == null || objects.length == 0) {
      throw new IllegalStateException(AT_LEAST_ONE_MATCHER_MESSAGE);
    }

    Matcher[] matchers = new Matcher[objects.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = convertToMatcher(objects[i]);
    }
    return matchers;
  }

  @SuppressWarnings("rawtypes")
  protected static Matcher convertToMatcher(Object object) {
    if (object == null) {
      throw new IllegalStateException("Null is not a valid matcher.");
    }
    final Matcher matcher;
    if (object instanceof String) {
      matcher = getCachedMatcher(new TokenValueMatcher((String) object));
    } else if (object instanceof TokenType) {
      TokenType tokenType = (TokenType) object;
      matcher = getCachedMatcher(new TokenTypeMatcher(tokenType, tokenType.hasToBeSkippedFromAst(null)));
    } else if (object instanceof RuleDefinition) {
      matcher = ((RuleDefinition) object).getRule();
    } else if (object instanceof Class) {
      matcher = getCachedMatcher(new TokenTypeClassMatcher((Class) object));
    } else if (object instanceof Matcher) {
      matcher = (Matcher) object;
    } else {
      throw new IllegalStateException("The matcher object can't be anything else than a Rule, Matcher, String, TokenType or Class. Object = " + object);
    }
    return matcher;
  }

}
