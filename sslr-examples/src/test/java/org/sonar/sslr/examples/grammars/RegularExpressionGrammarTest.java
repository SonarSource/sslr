/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.sslr.examples.grammars;

import com.sonar.sslr.api.Grammar;
import org.junit.Test;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class RegularExpressionGrammarTest {

  private Grammar g = RegularExpressionGrammar.create();

  @Test
  public void character_class() {
    assertThat(g.rule(RegularExpressionGrammar.CHARACTER_CLASS))
      /* simple class */
      .matches(correct("[abc]"))
      .matches(correct("[&]"))
      .matches(correct("[\\^]"))
      .matches(correct("[\\]]"))
      .matches(correct("[\\[]"))
      /* negation */
      .matches(correct("[^abc]"))
      .matches(correct("[^^]"))
      /* range */
      .matches(correct("[a-z]"))
      .matches(correct("[a-zA-Z]"))
      /* union */
      .matches(correct("[a-d[m-p]]"))
      .matches(correct("[[a-z]]"))
      /* intersection */
      .matches(correct("[a-z&&[def]]"))
      .matches(correct("[&&[a-z]]"))
      /* subtraction */
      .matches(correct("[a-z&&[^bc]]"))
      .matches(correct("[a-z&&[^m-p]]"))
      /* incorrect */
      .notMatches(incorrect("[]"))
      .notMatches(incorrect("[\\]"))
      .notMatches(incorrect("[[]"))
      .notMatches(incorrect("[a[]"))
      .notMatches(incorrect("[^]"))
      .notMatches(incorrect("[&&]"));
  }

  @Test
  public void atom() {
    assertThat(g.rule(RegularExpressionGrammar.ATOM))
      /* character */
      .matches(correct("a"))
      .matches(correct("]"))
      /* character class */
      .matches(correct("[a-z]"))
      /* any character */
      .matches(correct("."))
      /* back reference */
      .matches(correct("\\1"))
      /* incorrect */
      .notMatches(incorrect("\\0"))
      .notMatches(incorrect("["))
      .notMatches(incorrect("("))
      .notMatches(incorrect(")"))
      .notMatches(incorrect("*"))
      .notMatches(incorrect("+"))
      .notMatches(incorrect("?"))
      .notMatches("|");
  }

  @Test
  public void term() {
    assertThat(g.rule(RegularExpressionGrammar.TERM))
      /* atom */
      .matches(correct("a"))
      /* atom with quantifier */
      .matches(correct("a++"))
      /* capturing group */
      .matches(correct("()"))
      /* non-capturing group */
      .matches(correct("(?:)"))
      /* zero-width positive lookahead */
      .matches(correct("(?=)"))
      /* zero-width negative lookahead */
      .matches(correct("(?!)"));
  }

  @Test
  public void alternation() {
    assertThat(g.rule(RegularExpressionGrammar.ALTERNATION))
      .matches("")
      .matches(correct("a|b"))
      .matches(correct("a|b|c"))
      .matches(correct("|"));
  }

  @Test
  public void quantifier() {
    assertThat(g.rule(RegularExpressionGrammar.QUANTIFIER))
      /* greedy */
      .matches("*")
      .matches("+")
      .matches("?")
      .matches("{1}")
      .matches("{1,}")
      .matches("{1,2}")
      /* reluctant */
      .matches("*?")
      .matches("+?")
      .matches("??")
      .matches("{1}?")
      .matches("{1,}?")
      .matches("{1,2}?")
      /* possessive */
      .matches("*+")
      .matches("++")
      .matches("?+")
      .matches("{1}+")
      .matches("{1,}+")
      .matches("{1,2}+");
  }

  private static String incorrect(String pattern) {
    try {
      Pattern.compile(pattern);
      throw new AssertionError(pattern);
    } catch (PatternSyntaxException e) {
      return pattern;
    }
  }

  private static String correct(String pattern) {
    return Pattern.compile(pattern).pattern();
  }

}
