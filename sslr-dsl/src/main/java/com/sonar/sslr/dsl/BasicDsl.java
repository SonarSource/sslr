/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.dsl.DefaultDslTokenType.INTEGER;
import static com.sonar.sslr.dsl.DefaultDslTokenType.LITERAL;
import static com.sonar.sslr.dsl.DefaultDslTokenType.WORD;
import static com.sonar.sslr.impl.matcher.Matchers.o2n;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.matcher.RuleImpl;

public abstract class BasicDsl extends Dsl {

  public Rule translationUnit = new RuleImpl("translationUnit");
  public Rule statement = new RuleImpl("statement");
  public Rule word = new RuleImpl("word");
  public Rule literal = new RuleImpl("literal");
  public Rule integer = new RuleImpl("integer");

  public BasicDsl() {
    translationUnit.is(o2n(statement), EOF);

    word.is(WORD);
    literal.is(LITERAL);
    integer.is(INTEGER);
  }

  public final Rule getRootRule() {
    return translationUnit;
  }
}
