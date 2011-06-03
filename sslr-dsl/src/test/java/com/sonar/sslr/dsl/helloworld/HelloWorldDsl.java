/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.dsl.helloworld;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

public class HelloWorldDsl extends Grammar {

  public Rule translationUnit;
  public Rule command;
  public Rule message;

  public HelloWorldDsl() {
    translationUnit.is(o2n(command), EOF);
    command.is("print", message).plug(HelloWorld.class);
    message.is(LITERAL).plug(String.class);
  }

  @Override
  public Rule getRootRule() {
    return translationUnit;
  }
}
