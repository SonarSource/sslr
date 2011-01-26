/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.dsl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.ast.AstWalker;

public class DslRunner {

  private final DslParser parser;

  private DslRunner(DslDefinition grammar) {
    parser = new DslParser(grammar);
  }

  public static void execute(DslDefinition dsl, String source) {
    AstNode ast = new DslParser(dsl).parse(source);
    AstWalker walker = new AstWalker();
    walker.walkAndVisit(ast);
  }

}
