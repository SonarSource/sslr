/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.dsl;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.dsl.bytecode.Bytecode;
import com.sonar.sslr.dsl.internal.Compiler;
import com.sonar.sslr.dsl.internal.DefaultDslLexer;
import com.sonar.sslr.impl.Parser;

public class DslRunner {

  private Bytecode bytecode;
  private Compiler compiler;
  private DslMemory memory;

  private DslRunner(Grammar dsl, String source) {
    compiler = new Compiler(Parser.builder(dsl).optSetLexer(new DefaultDslLexer()).build(), source);
  }

  private DslRunner inject(Object component) {
    compiler.inject(component);
    return this;
  }

  public DslRunner putInMemory(String variableName, Object value) {
    memory.put(variableName, value);
    return this;
  }

  public Object getFromMemory(String variableName) {
    return memory.get(variableName);
  }

  public DslRunner resetDslMemory() {
    memory = new DslMemory();
    inject(memory);
    return this;
  }

  public static DslRunner create(Grammar dsl, String source, Object... injectedComponents) {
    DslRunner dslRunner = new DslRunner(dsl, source);
    for (Object component : injectedComponents) {
      dslRunner.inject(component);
    }
    dslRunner.resetDslMemory();
    dslRunner.compile();
    return dslRunner;
  }

  private void compile() {
    bytecode = compiler.compile();
  }

  public void execute() {
    bytecode.execute();
  }
}
