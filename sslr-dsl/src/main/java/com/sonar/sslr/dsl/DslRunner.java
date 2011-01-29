/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.dsl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.dsl.internal.AdapterRepository;
import com.sonar.sslr.dsl.internal.AstToBytecodeTransformer;
import com.sonar.sslr.dsl.internal.Bytecode;
import com.sonar.sslr.dsl.internal.DefaultDslParser;
import com.sonar.sslr.impl.Parser;

public class DslRunner {

  private Parser<Grammar> parser;
  private Grammar dsl;
  private String source;
  private AdapterRepository adapters = new AdapterRepository();
  private Bytecode bytecode;

  private DslRunner(Dsl dsl, String source) {
    this.dsl = dsl;
    this.source = source;
  }

  public DslRunner inject(Object component) {
    adapters.inject(component);
    return this;
  }

  public static DslRunner create(Dsl dsl, String source) {
    DslRunner dslRunner = new DslRunner(dsl, source);
    dslRunner.compile();
    return dslRunner;
  }

  private void compile() {
    parser = new DefaultDslParser(dsl);
    AstNode ast = parser.parse(source);
    bytecode = new AstToBytecodeTransformer(adapters).transform(ast);
  }

  public void execute() {
    bytecode.execute();
  }

}
