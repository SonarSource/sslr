/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC.rules;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCParser;

public abstract class RuleTest {

  protected final Parser<MiniCGrammar> p = MiniCParser.create();
  protected final MiniCGrammar g = p.getGrammar();

  public final Rule getTestedRule() {
    return p.getRootRule();
  }

  public abstract void init();

}
