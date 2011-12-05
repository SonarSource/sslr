/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.language.rules;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.miniC.MiniLanguageGrammar;
import com.sonar.sslr.test.miniC.MiniLanguageParser;

public abstract class RuleTest {

  protected final Parser<MiniLanguageGrammar> p = MiniLanguageParser.create();
  protected final MiniLanguageGrammar g = p.getGrammar();

  public final Rule getTestedRule() {
    return p.getRootRule();
  }

  public abstract void init();

}
