/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonar.sslr.dsl;

import com.sonar.sslr.dsl.internal.Bytecode;
import com.sonar.sslr.dsl.internal.Compiler;

public class DslRunner {

  private Bytecode bytecode;
  private Compiler compiler;

  private DslRunner(Dsl dsl, String source) {
    compiler = new Compiler(dsl, source);
  }

  public DslRunner inject(Object component) {
    compiler.inject(component);
    return this;
  }

  public static DslRunner create(Dsl dsl, String source) {
    DslRunner dslRunner = new DslRunner(dsl, source);
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
