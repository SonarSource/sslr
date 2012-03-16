/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import com.sonar.sslr.api.GenericTokenType;
import org.junit.Test;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class GrammarFunctionsTest {

  @Test
  public void testCacheAndEqualsAndHashCode() {
    assertThat(and(opt(or(EOF, EOL)), IDENTIFIER) == and(opt(or(EOF, EOL)), IDENTIFIER), is(true));
    assertThat(
        and("LOOP", one2n("statement"), "END", "LOOP", opt(IDENTIFIER), "SEMICOLON") == and("LOOP", one2n("statement"), "END", "LOOP",
            opt(IDENTIFIER), "SEMICOLON"), is(true));
    assertThat(
        and(or("hehe", "huhu", and("haha", opt("hoho"))), IDENTIFIER,
            next("hehe", com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not(and("hmmm"))), GenericTokenType.class,
            longestOne("one", "two", "three")) == and(or("hehe", "huhu", and("haha", opt("hoho"))),
            IDENTIFIER, next("hehe", com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not(and("hmmm"))), GenericTokenType.class,
            longestOne("one", "two", "three")), is(true));
  }

}
