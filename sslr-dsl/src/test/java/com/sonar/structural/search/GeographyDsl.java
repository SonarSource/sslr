/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import static com.sonar.sslr.dsl.DefaultDslTokenType.WORD;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.internal.DefaultDslLexer;
import com.sonar.sslr.impl.Parser;
import static com.sonar.sslr.api.GenericTokenType.*;

public class GeographyDsl extends Grammar {

  public static final Parser<GeographyDsl> geographyParser = Parser.builder(new GeographyDsl()).optSetLexer(new DefaultDslLexer()).build();

  public Rule world;
  public Rule nation;
  public Rule capital;

  public GeographyDsl() {
    world.is(one2n(nation), EOF);
    nation.is(capital);
    capital.is(WORD);
  }

  @Override
  public Rule getRootRule() {
    return world;
  }

}
