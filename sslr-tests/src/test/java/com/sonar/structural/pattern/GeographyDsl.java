/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.pattern;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.channel.RegexpChannel;

import static com.sonar.sslr.dsl.DslTokenType.WORD;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;

public class GeographyDsl extends Grammar {

  public static final GeographyDsl grammar = new GeographyDsl();
  public static final Parser<GeographyDsl> geographyParser = Parser.builder(grammar)
      .withLexer(Lexer.builder().withChannel(new RegexpChannel(WORD, "\\p{Alpha}[\\p{Alpha}\\d_]+")).build()).build();

  public Rule world;
  public Rule nation;
  public Rule capital;

  public GeographyDsl() {
    world.is(one2n(nation));
    nation.is(capital);
    capital.is(WORD);
  }

  @Override
  public Rule getRootRule() {
    return world;
  }

}
