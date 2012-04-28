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
