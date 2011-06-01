/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.TokenType;

/**
 * This class provides all the functions to define a context-free grammar
 */
public class GrammarFunctions {

  private GrammarFunctions() {
  };

  public static final class Standard {

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
      Matcher[] matchers = convertToMatchers(objects);
      if (matchers.length == 0) {
        throw new IllegalStateException("You must define at least one matcher.");
      } else if (matchers.length == 1) {
        return new ZeroToNMatcher(matchers[0]);
      } else {
        return new ZeroToNMatcher(new AndMatcher(matchers));
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
      Matcher[] matchers = convertToMatchers(elements);
      if (matchers.length == 0) {
        throw new IllegalStateException("You must define at least one matcher.");
      } else if (matchers.length == 1) {
        return new OneToNMatcher(matchers[0]);
      } else {
        return new OneToNMatcher(new AndMatcher(matchers));
      }
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
        return new OptMatcher(convertToMatcher(elements[0]));
      }
      return new OptMatcher(new AndMatcher(convertToMatchers(elements)));
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
      return new OrMatcher(convertToMatchers(elements));
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
        return convertToMatcher(elements[0]);
      }
      return new AndMatcher(convertToMatchers(elements));
    }
  }

  public static final class Predicate {

    /**
     * Syntactic predicate to check that the next tokens don't match an element.
     */
    public static Matcher not(Object element) {
      return new NotMatcher(convertToMatcher(element));
    }

    /**
     * Syntactic predicate to check that the next tokens match some elements.
     */
    public static Matcher next(Object... elements) {
      if (elements.length == 1) {
        return new NextMatcher(convertToMatcher(elements[0]));
      }
      return new NextMatcher(new AndMatcher(convertToMatchers(elements)));
    }
  }

  public static final class Advanced {

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
      return new AdjacentMatcher(convertToMatcher(element));
    }

    /**
     * Consume the next token if and only if the element doesn't match
     */
    public static Matcher anyTokenButNot(Object element) {
      return new AnyTokenButNotMatcher(convertToMatcher(element));
    }

    /**
     * Match the next token if and only if its type belongs to the provided list
     */
    public static Matcher isOneOfThem(TokenType... types) {
      return new TokenTypesMatcher(types);
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
      return new BridgeMatcher(from, to);
    }

    /**
     * For unit test only Consume the next token whatever it is
     */
    public static Matcher isTrue() {
      return new BooleanMatcher(true);
    }

    /**
     * For unit test only Not consume the next token whatever it is
     */
    public static Matcher isFalse() {
      return new BooleanMatcher(false);
    }

    /**
     * Consume the next token whatever it is
     */
    public static Matcher anyToken() {
      return new AnyTokenMatcher();
    }

    /**
     * Consume every following token which are on the current line
     */
    public static Matcher tillNewLine() {
      return new TillNewLineMatcher();
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
      return new InclusiveTillMatcher(convertToMatcher(element));
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
      return new ExclusiveTillMatcher(convertToMatchers(elements));
    }

    /**
     * Consume the next token if and only if its type and its value match the provided ones.
     */
    public static Matcher token(TokenType type, String value) {
      return new TokenTypeAndValueMatcher(type, value);
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
      return new AtLeastOneMatcher(convertToMatchers(elements));
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
      return new LongestOneMatcher(convertToMatchers(elements));
    }
  }

  protected static final Matcher[] convertToMatchers(Object[] objects) {
    Matcher[] matchers = new Matcher[objects.length];
    for (int i = 0; i < matchers.length; i++) {
      matchers[i] = convertToMatcher(objects[i]);
    }
    return matchers;
  }

  @SuppressWarnings("rawtypes")
  protected static final Matcher convertToMatcher(Object object) {
    Matcher matcher;
    if (object instanceof String) {
      matcher = new TokenValueMatcher((String) object);
    } else if (object instanceof TokenType) {
      TokenType tokenType = (TokenType) object;
      matcher = new TokenTypeMatcher(tokenType, tokenType.hasToBeSkippedFromAst(null));
    } else if (object instanceof RuleDefinition) {
      matcher = ((RuleDefinition) object).getRule();
    } else if (object instanceof Class) {
      matcher = new TokenTypeClassMatcher((Class) object);
    } else {
      try {
        matcher = (Matcher) object;
      } catch (ClassCastException e) {
        throw new IllegalStateException("The matcher object can't be anything else than a Matcher, String or TokenType. Object = " + object);
      }
    }

    return matcher;
  }

}
