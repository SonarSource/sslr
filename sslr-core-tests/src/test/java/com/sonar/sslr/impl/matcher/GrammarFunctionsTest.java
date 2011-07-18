/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.matcher;

import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Advanced.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.*;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sonar.sslr.api.GenericTokenType;

public class GrammarFunctionsTest {

	@Test
	public void testEqualsAndHashCode() {
		assertThat(and(opt(or(EOF, EOL)), IDENTIFIER) == and(opt(or(EOF, EOL)), IDENTIFIER), is(true));
		assertThat(and("LOOP", one2n("statement"), "END", "LOOP", opt(IDENTIFIER), "SEMICOLON") == and("LOOP", one2n("statement"), "END", "LOOP", opt(IDENTIFIER), "SEMICOLON"), is(true));
		assertThat(and(or("hehe", "huhu", and("haha", opt("hoho"))), IDENTIFIER, next("hehe", not(and("hmmm"))), GenericTokenType.class, longestOne("one", "two", "three"), atLeastOne("alt1", "alt2", "alt3")) == and(or("hehe", "huhu", and("haha", opt("hoho"))), IDENTIFIER, next("hehe", not(and("hmmm"))), GenericTokenType.class, longestOne("one", "two", "three"), atLeastOne("alt1", "alt2", "alt3")), is(true));
	}
	
}
